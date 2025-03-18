pipeline {
    agent any

    environment {
        // AWS ECR
        AWS_ECR_CREDENTIAL_ID = 'aws-ecr-credentials'
        AWS_ECR_URI = '975050092378.dkr.ecr.ap-northeast-2.amazonaws.com/clover/cicd'
        AWS_REGION = 'ap-northeast-2'
    }

    stages {
        stage('Checkout Source Code with Submodules') {
            steps {
                script {
                    echo 'Checking out the repository with submodules'
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        // Git 클론 및 서브모듈 초기화
                        sh """
                            git config --global credential.helper 'store'
                            git submodule update --init --recursive
                        """
                    }
                }
            }
        }

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
            parallel {
                stage('Build & Push medicine') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('medicine') }
                    }
                    steps {
                        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_ECR_CREDENTIAL_ID}"]]) {
                            script {
                                def dockerfilePath = "src/main/java/com/eodi/yak/eodi_yak/domain/medicine/Dockerfile"
                                def buildContext = "${WORKSPACE}"
                                def imageTag = "${AWS_ECR_URI}/medicine:${BUILD_NUMBER}"

                                sh 'pwd'
                                echo "WORKSPACE : ${WORKSPACE}"

                                sh """
                                    echo "Current working directory: \$(pwd)"
                                    echo "Dockerfile Path: ${dockerfilePath}"
                                    echo "Build Context: ${buildContext}"

                                    docker buildx build --no-cache -t ${imageTag} -f \"${dockerfilePath}\" \"${buildContext}\"
                                    aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ECR_URI}
                                    docker push ${imageTag}
                                """

                            }
                        }
                    }
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
