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
                                def imageTag = "${AWS_ECR_URI}:eodiyak-backend-medicine-${BUILD_NUMBER}"

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
                
                stage('Deploy Backend Image (medicine) with Updating Manifest Repository') {
                    steps {
                        script {
                            // SSH 에이전트 활성화
                            withEnv(["GIT_SSH_COMMAND=ssh -i /var/lib/jenkins/.ssh/id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"]) {
                                sh '''
                                set -e

                                # 작업 디렉토리 설정
                                WORK_DIR=$(mktemp -d)
                                cd $WORK_DIR

                                # Manifest Repo Clone
                                git clone git@github.com:Clover-CloudOver/Eodi-yak-manifest.git
                                cd Eodi-yak-manifest

                                # Image Tag 변경 (변수 확장을 명확하게 하기 위해 sh 내에서 사용)
                                sed -i "s|image: $AWS_ECR_URI.*|image: $AWS_ECR_URI:eodiyak-backend-medicine-$BUILD_NUMBER|" backend-medicine.yaml

                                # 변경 사항 Commit & Push
                                git config user.name "1006lem"
                                git config user.email "lemon6565@naver.com"
                                git add backend-medicine.yaml
                                git commit -m "Update image tag to eodiyak-backend-medicine-$BUILD_NUMBER"
                                git push origin main
                                '''
                            }
                        }
                    }
                }

                stage('Build & Push member') {
                     when {
                         expression { return env.CHANGED_SERVICES.contains('member') }
                     }
                     steps {
                         withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_ECR_CREDENTIAL_ID}"]]) {
                             script {
                                 def dockerfilePath = "src/main/java/com/eodi/yak/eodi_yak/domain/member/Dockerfile"
                                 def buildContext = "${WORKSPACE}"
                                 def imageTag = "${AWS_ECR_URI}:eodiyak-backend-member-${BUILD_NUMBER}"
 
                                 sh """
                                     docker buildx build --no-cache -t ${imageTag} -f \"${dockerfilePath}\" \"${buildContext}\"
                                     aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ECR_URI}
                                     docker push ${imageTag}
                                 """
                             }
                         }
                     }
                }

                stage('Deploy Backend Image (member) with Updating Manifest Repository') {
                    steps {
                        script {
                            // SSH 에이전트 활성화
                            withEnv(["GIT_SSH_COMMAND=ssh -i /var/lib/jenkins/.ssh/id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"]) {
                                sh '''
                                set -e

                                # 작업 디렉토리 설정
                                WORK_DIR=$(mktemp -d)
                                cd $WORK_DIR

                                # Manifest Repo Clone
                                git clone git@github.com:Clover-CloudOver/Eodi-yak-manifest.git
                                cd Eodi-yak-manifest

                                # Image Tag 변경 (변수 확장을 명확하게 하기 위해 sh 내에서 사용)
                                sed -i "s|image: $AWS_ECR_URI.*|image: $AWS_ECR_URI:eodiyak-backend-member-$BUILD_NUMBER|" backend-member.yaml

                                # 변경 사항 Commit & Push
                                git config user.name "1006lem"
                                git config user.email "lemon6565@naver.com"
                                git add backend-member.yaml
                                git commit -m "Update image tag to eodiyak-backend-member-$BUILD_NUMBER"
                                git push origin main
                                '''
                            }
                        }
                    }
                }


                stage('Build & Push pharmacy') {
                     when {
                         expression { return env.CHANGED_SERVICES.contains('pharmacy') }
                     }
                     steps {
                         withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_ECR_CREDENTIAL_ID}"]]) {
                             script {
                                 def dockerfilePath = "src/main/java/com/eodi/yak/eodi_yak/domain/pharmacy/Dockerfile"
                                 def buildContext = "${WORKSPACE}"
                                 def imageTag = "${AWS_ECR_URI}:eodiyak-backend-pharmacy-${BUILD_NUMBER}"
 
                                 sh """
                                     docker buildx build --no-cache -t ${imageTag} -f \"${dockerfilePath}\" \"${buildContext}\"
                                     aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ECR_URI}
                                     docker push ${imageTag}
                                 """
                             }
                         }
                     }
                 }

                stage('Deploy Backend Image (pharmacy) with Updating Manifest Repository') {
                    steps {
                        script {
                            // SSH 에이전트 활성화
                            withEnv(["GIT_SSH_COMMAND=ssh -i /var/lib/jenkins/.ssh/id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"]) {
                                sh '''
                                set -e

                                # 작업 디렉토리 설정
                                WORK_DIR=$(mktemp -d)
                                cd $WORK_DIR

                                # Manifest Repo Clone
                                git clone git@github.com:Clover-CloudOver/Eodi-yak-manifest.git
                                cd Eodi-yak-manifest

                                # Image Tag 변경 (변수 확장을 명확하게 하기 위해 sh 내에서 사용)
                                sed -i "s|image: $AWS_ECR_URI.*|image: $AWS_ECR_URI:eodiyak-backend-pharmacy-$BUILD_NUMBER|" backend-pharmacy.yaml

                                # 변경 사항 Commit & Push
                                git config user.name "1006lem"
                                git config user.email "lemon6565@naver.com"
                                git add backend-pharmacy.yaml
                                git commit -m "Update image tag to eodiyak-backend-pharmacy-$BUILD_NUMBER"
                                git push origin main
                                '''
                            }
                        }
                    }
                }
 
                 stage('Build & Push reservation') {
                     when {
                         expression { return env.CHANGED_SERVICES.contains('reservation') }
                     }
                     steps {
                         withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_ECR_CREDENTIAL_ID}"]]) {
                             script {
                                 def dockerfilePath = "src/main/java/com/eodi/yak/eodi_yak/domain/reservation/Dockerfile"
                                 def buildContext = "${WORKSPACE}"
                                 def imageTag = "${AWS_ECR_URI}:eodiyak-backend-reservation-${BUILD_NUMBER}"
 
                                 sh """
                                     docker buildx build --no-cache -t ${imageTag} -f \"${dockerfilePath}\" \"${buildContext}\"
                                     aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ECR_URI}
                                     docker push ${imageTag}
                                 """
                             }
                         }
                     }
                 }

                stage('Deploy Backend Image (reservation) with Updating Manifest Repository') {
                    steps {
                        script {
                            // SSH 에이전트 활성화
                            withEnv(["GIT_SSH_COMMAND=ssh -i /var/lib/jenkins/.ssh/id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no"]) {
                                sh '''
                                set -e

                                # 작업 디렉토리 설정
                                WORK_DIR=$(mktemp -d)
                                cd $WORK_DIR

                                # Manifest Repo Clone
                                git clone git@github.com:Clover-CloudOver/Eodi-yak-manifest.git
                                cd Eodi-yak-manifest

                                # Image Tag 변경 (변수 확장을 명확하게 하기 위해 sh 내에서 사용)
                                sed -i "s|image: $AWS_ECR_URI.*|image: $AWS_ECR_URI:eodiyak-backend-reservation-$BUILD_NUMBER|" backend-reservation.yaml

                                # 변경 사항 Commit & Push
                                git config user.name "1006lem"
                                git config user.email "lemon6565@naver.com"
                                git add backend-reservation.yaml
                                git commit -m "Update image tag to eodiyak-backend-reservation-$BUILD_NUMBER"
                                git push origin main
                                '''
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
