pipeline {
    agent any

    tools {
        maven 'M2_HOME'       // Ensure "M2_HOME" is configured in Jenkins
        jdk 'JAVA_HOME'       // Ensure "JAVA_HOME" is configured in Jenkins
    }

    environment {
        GIT_URL = 'https://github.com/BoshAF77/Devops.git'
        GIT_BRANCH = 'Devops'
        CREDENTIALS_ID = 'GitHub_Credentials'
        DOCKER_IMAGE_NAME = 'anisfarjallah7/alpine'
        DOCKER_CREDENTIALS_ID = 'Docker_Credentials'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git branch: "${env.GIT_BRANCH}",
                    url: "${env.GIT_URL}",
                    credentialsId: "${env.CREDENTIALS_ID}"
            }
        }

        stage('Get Version') {
            steps {
                script {
                    env.APP_VERSION = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout: true).trim()
                    echo "Application version: ${env.APP_VERSION}"
                }
            }
        }

        stage('Mockito Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'
            }
        }

        stage('Verify JAR File') {
            steps {
                script {
                    def jarFile = sh(script: 'ls -1 target/*.jar', returnStdout: true).trim()
                    if (!jarFile) {
                        error("No JAR file found!")
                    } else {
                        echo "JAR file found: ${jarFile}"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    def dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${env.APP_VERSION}")
                    echo "Built Docker Image: ${dockerImage.id}"
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID,
                                                     usernameVariable: 'DOCKERHUB_USERNAME',
                                                     passwordVariable: 'DOCKERHUB_PASSWORD')]) {
                        sh '''
                            echo "$DOCKERHUB_PASSWORD" | docker login -u "$DOCKERHUB_USERNAME" --password-stdin
                        '''
                        sh "docker push ${DOCKER_IMAGE_NAME}:${env.APP_VERSION}"
                        sh "docker logout"
                    }
                }
            }
        }

        stage('Debug Workspace') {
            steps {
                sh 'ls -l ${WORKSPACE}'
            }
        }

        stage('Docker compose (BackEnd MySql)') {
            steps {
                script {
                    sh 'docker compose -f ${WORKSPACE}/Docker-compose.yml up -d'
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }
        success {
            echo 'Pipeline executed successfully!'
            mail to: 'anisfarjallah0705@gmail.com',
                subject: "Jenkins Pipeline Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Good news! The pipeline ${env.JOB_NAME} has completed successfully. Check the details here: ${env.BUILD_URL}"
        }
        failure {
            echo 'Pipeline failed.'
            mail to: 'anisfarjallah0705@gmail.com',
                subject: "Jenkins Pipeline Failure: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Unfortunately, the pipeline ${env.JOB_NAME} has failed. Please check the details here: ${env.BUILD_URL}"
        }
    }
}
