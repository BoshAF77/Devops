pipeline {
    agent {
        docker { image 'docker:latest' } // Use Docker agent for the build stage
    }

    tools {
        maven 'M2_HOME'       // Ensure "M2_HOME" is configured in Jenkins
        jdk 'JAVA_HOME'       // Ensure "JAVA_HOME" is configured in Jenkins
    }

    environment {
        GIT_URL = 'https://github.com/BoshAF77/Devops.git'
        GIT_BRANCH = 'Devops'
        CREDENTIALS_ID = 'GitHub_Credentials'
        DOCKER_IMAGE_NAME = 'alpine-container'
    }

    stages {
        stage('Checkout Code') {
            steps {
                git(
                    branch: "${env.GIT_BRANCH}",
                    url: "${env.GIT_URL}",
                    credentialsId: "${env.CREDENTIALS_ID}"
                )
            }
        }

        stage('Get Version') {
            steps {
                script {
                    env.APP_VERSION = sh(
                        script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout",
                        returnStdout: true
                    ).trim()
                    echo "Application version: ${env.APP_VERSION}"
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'  // Compile and package the JAR
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

        stage('Build Docker Image (Spring Part)') {
            steps {
                script {
                    docker.withRegistry('', '') { // Use default Docker registry
                        def dockerImage = docker.build("${DOCKER_IMAGE_NAME}:${env.APP_VERSION}")
                        echo "Built Docker Image: ${dockerImage.id}"
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withCredentials([string(credentialsId: 'SonarQube_Token', variable: 'SONAR_TOKEN')]) {
                    sh 'mvn sonar:sonar -Dsonar.login=$SONAR_TOKEN'
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    withEnv(["MAVEN_HOME=${tool 'M2_HOME'}"]) {
                        sh 'mvn deploy -s /usr/share/maven/conf/settings.xml'
                    }
                }
            }
        }
    }
}
