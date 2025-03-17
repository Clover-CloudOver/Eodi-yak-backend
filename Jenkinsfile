pipeline {
    agent any

    environment {
        // AWS ECR
        AWS_ECR_CREDENTIAL_ID = 'aws-ecr-credentials'
        AWS_ECR_URI = '535532591444.dkr.ecr.ap-northeast-2.amazonaws.com/clover/cicd'
        AWS_REGION = 'ap-northeast-2'
    }

    stages {
        stage('Checkout Source Code with Submodules') {
            steps {
                script {
                    echo 'Checking out the repository with submodules'
                    withCredentials([usernamePassword(credentialsId: 'github-credentials', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
                        sh """
                            git config --global credential.helper 'store'
                            git submodule update --init --recursive
                            git submodule update --remote
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

        stage('Move Submodule to src/main/resources') {
            steps {
                echo 'Copying submodule files to src/main/resources'
                sh 'cp -r business-card-config/* src/main/resources/'
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
                                    docker build -t ${imageTag} -f ${dockerfilePath} ${buildContext}
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
