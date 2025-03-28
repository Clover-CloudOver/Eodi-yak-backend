pipeline {
    agent any

    environment {
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
                        sh """
                            git config --global credential.helper 'store'
                            git submodule update --init --remote --recursive
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
                        script {
                            buildAndPushDockerImage('medicine', "src/main/java/com/eodi/yak/eodi_yak/domain/medicine/Dockerfile", "${AWS_ECR_URI}:eodiyak-backend-medicine-${BUILD_NUMBER}")
                        }
                    }
                }
                stage('Deploy Backend Image (medicine)') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('medicine') }
                    }
                    steps {
                        script {
                            deployManifest('medicine', 'backend-medicine.yaml')
                        }
                    }
                }
                stage('Build & Push member') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('member') }
                    }
                    steps {
                        script {
                            buildAndPushDockerImage('member', "src/main/java/com/eodi/yak/eodi_yak/domain/member/Dockerfile", "${AWS_ECR_URI}:eodiyak-backend-member-${BUILD_NUMBER}")
                        }
                    }
                }
                stage('Deploy Backend Image (member)') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('member') }
                    }
                    steps {
                        script {
                            deployManifest('member', 'backend-member.yaml')
                        }
                    }
                }
                stage('Build & Push pharmacy') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('pharmacy') }
                    }
                    steps {
                        script {
                            buildAndPushDockerImage('pharmacy', "src/main/java/com/eodi/yak/eodi_yak/domain/pharmacy/Dockerfile", "${AWS_ECR_URI}:eodiyak-backend-pharmacy-${BUILD_NUMBER}")
                        }
                    }
                }
                stage('Deploy Backend Image (pharmacy)') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('pharmacy') }
                    }
                    steps {
                        script {
                            deployManifest('pharmacy', 'backend-pharmacy.yaml')
                        }
                    }
                }
                stage('Build & Push reservation') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('reservation') }
                    }
                    steps {
                        script {
                            buildAndPushDockerImage('reservation', "src/main/java/com/eodi/yak/eodi_yak/domain/reservation/Dockerfile", "${AWS_ECR_URI}:eodiyak-backend-reservation-${BUILD_NUMBER}")
                        }
                    }
                }
                stage('Deploy Backend Image (reservation)') {
                    when {
                        expression { return env.CHANGED_SERVICES.contains('reservation') }
                    }
                    steps {
                        script {
                            deployManifest('reservation', 'backend-reservation.yaml')
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

def buildAndPushDockerImage(serviceName, dockerfilePath, imageTag) {
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: "${AWS_ECR_CREDENTIAL_ID}"]]) {
        script {
            def buildContext = "${WORKSPACE}"
            sh """
                docker buildx build --no-cache -t ${imageTag} -f \"${dockerfilePath}\" \"${buildContext}\"
                aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ECR_URI}
                docker push ${imageTag}
            """
        }
    }
}


def deployManifest(serviceName, manifestFile) {
    withEnv([
        "GIT_SSH_COMMAND=ssh -i /var/lib/jenkins/.ssh/id_rsa -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no",
        "AWS_ECR_URI=${AWS_ECR_URI}",  // AWS_ECR_URI를 파이프라인 내에서 정의하기
        "BUILD_NUMBER=${BUILD_NUMBER}"  // Jenkins의 기본 BUILD_NUMBER 환경 변수를 사용
    ]) {
        sh """
        set -e
        WORK_DIR=\$(mktemp -d) 
        cd \$WORK_DIR
        git clone git@github.com:Clover-CloudOver/Eodi-yak-manifest.git
        cd Eodi-yak-manifest
        # 최신 변경 사항 가져오기
        git pull --rebase origin main
        sed -i "s|image: ${AWS_ECR_URI}.*|image: ${AWS_ECR_URI}:eodiyak-backend-${serviceName}-${BUILD_NUMBER}|" ${manifestFile}
        git config user.name "1006lem"
        git config user.email "lemon6565@naver.com"
        git add ${manifestFile}
        git commit -m "Update image tag to eodiyak-backend-${serviceName}-${BUILD_NUMBER}"
        git push origin msa
        """
    }
}

