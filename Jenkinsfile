pipeline {
    agent any

    environment {
        PATH = "/opt/homebrew/bin:/usr/local/bin:/usr/bin:/bin:/usr/sbin:/sbin"

        // Application secrets
        SPRING_DATASOURCE_PASSWORD = credentials('civicpulse-db-password')
        JWT_SECRET = credentials('civicpulse-jwt-secret')
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''
                    echo "Building AuthService..."

                    ./mvnw clean compile
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                    echo "Running AuthService tests..."

                    ./mvnw test
                '''
            }
        }

        stage('Secret Scan') {
            steps {
                sh '''
                    echo "Running Trivy secret scan..."

                    trivy fs \
                      --scanners secret \
                      --skip-dirs target \
                      --exit-code 1 \
                      .
                '''
            }
        }

        stage('Verify Docker') {
            steps {
                sh '''
                    echo "Docker location:"
                    which docker

                    echo "Docker version:"
                    docker --version
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    echo "Building AuthService Docker image..."

                    docker build \
                      -t civicpulse-auth-service:${BUILD_NUMBER} .
                '''
            }
        }

        stage('Docker Image Scan') {
            steps {
                sh '''
                    echo "Scanning AuthService Docker image..."

                    trivy image \
                      --severity HIGH,CRITICAL \
                      civicpulse-auth-service:${BUILD_NUMBER}
                '''
            }
        }

        stage('Docker Push') {
            steps {

                withCredentials([
                    usernamePassword(
                        credentialsId: 'dockerhub-civicpulse',
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {

                    sh '''
                        echo "Logging in to Docker Hub..."

                        echo "$DOCKER_PASSWORD" | docker login \
                          -u "$DOCKER_USERNAME" \
                          --password-stdin

                        echo "Tagging AuthService image..."

                        docker tag \
                          civicpulse-auth-service:${BUILD_NUMBER} \
                          ${DOCKER_USERNAME}/civicpulse-auth-service:${BUILD_NUMBER}

                        echo "Pushing AuthService image..."

                        docker push \
                          ${DOCKER_USERNAME}/civicpulse-auth-service:${BUILD_NUMBER}

                        echo "Logging out from Docker Hub..."

                        docker logout
                    '''
                }
            }
        }
    }

    post {

        success {
            echo '''
            ==========================================
            AuthService CI Pipeline SUCCESS
            ==========================================
            Docker Image:
            ${DOCKER_USERNAME}/civicpulse-auth-service:${BUILD_NUMBER}
            ==========================================
            '''
        }

        failure {
            echo '''
            ==========================================
            AuthService CI Pipeline FAILED
            ==========================================
            Check the failed stage and Jenkins logs.
            ==========================================
            '''
        }

        always {
            echo "Build Number: ${BUILD_NUMBER}"
        }
    }
}