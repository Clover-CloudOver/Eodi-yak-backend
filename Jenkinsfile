pipeline {
    agent any

    environment {
        // AWS ECR
        AWS_ECR_CREDENTIAL_ID = 'aws-ecr-credentials'
        AWS_ECR_URI = '535532591444.dkr.ecr.ap-northeast-2.amazonaws.com/clover/cicd'
        AWS_REGION = 'ap-northeast-2'
    }

    stages {

        stage('Detect Changes') {
            steps {
                script {
                    def changedFiles = sh(script: "git diff --name-only HEAD~1 HEAD", returnStdout: true).trim().split("\n")
                    def changedServices = []
                    def commonChanged = false

                    // MSA 서비스 폴더 리스트
                    def services = ['medicine', 'member', 'pharmacy', 'reservation']
                    def msaFolders = services.collect { "src/main/java/com/eodi/yak/eodi_yak/domain/${it}/" }

                    for (file in changedFiles) {
                        boolean isMSAFile = msaFolders.any { file.startsWith(it) }
                        if (!isMSAFile) {
                            commonChanged = true
                            break
                        }
                    }

                    if (commonChanged) {
                        changedServices = services
                    } else {
                        for (service in services) {
                            if (changedFiles.any { it.startsWith("src/main/java/com/eodi/yak/eodi_yak/domain/${service}/") }) {
                                changedServices.add(service)
                            }
                        }
                    }

                    env.CHANGED_SERVICES = changedServices.join(",")
                    echo "Changed Services: ${env.CHANGED_SERVICES}"
                }
            }
        }

        stage('Build & Push Services') {
            steps {
                script {
                    def services = env.CHANGED_SERVICES.split(",")
                    def parallelStages = [:]

                    for (service in services) {
                        parallelStages["Build & Push ${service}"] = {
                                steps {
                                    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_ECR_CREDENTIAL_ID}"]]) {
                                        script {
                                            def dockerfilePath = "src/main/java/com/eodi/yak/eodi_yak/domain/${service}/Dockerfile"
                                            def buildContext = "${WORKSPACE}"  // Backend 루트를 context로 설정
                                            def imageTag = "${AWS_ECR_URI}/${service}:${BUILD_NUMBER}"

                                            sh """
                                                docker build -t ${imageTag} -f ${dockerfilePath} ${buildContext}
                                                aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ECR_URI}
                                                docker push ${imageTag}
                                            """
                                        }
                                    }
                            }
                        }
                    }

                    parallel parallelStages
                }
            }
        }

        stage('Clean Up Docker Images on Jenkins Server') {
            steps {
                echo 'Cleaning up unused Docker images on Jenkins server'
                sh "docker image prune -f --all"
            }
        }
    }

    post {
        success {
            echo 'Backend MSA Build & Push Pipeline succeeded'
        }
        failure {
            echo 'Backend MSA Build & Push Pipeline failed'
        }
    }
}
