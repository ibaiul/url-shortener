@Library('my-pipeline-utils') _
def NAME="url-shortener"

pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr:'10'))
    }
    environment {
        BRANCH = GIT_BRANCH.minus('origin/')
        DOCKER_REGISTRY = "ibaiul/urlshortener"
        DOCKER_REGISTRY_CREDENTIAL = 'docker-hub'
        dockerImage = ''
    }

    stages {
        stage('Build') {
            steps {
                withEnv(['JAVA_HOME=/usr/lib/jvm/java-11-openjdk']) {
                    withMaven(maven: 'Maven 3.5') {
                        sh "mvn clean compile"
                    }
                }
            }
        }

        stage('Unit tests') {
            steps {
                withEnv(['JAVA_HOME=/usr/lib/jvm/java-11-openjdk']) {
                    withMaven(maven: 'Maven 3.5') {
                        sh "mvn test"
                    }
                }
            }
        }

        stage('Integration tests') {
            steps {
                withEnv(['JAVA_HOME=/usr/lib/jvm/java-11-openjdk']) {
                    withMaven(maven: 'Maven 3.5') {
                        sh "mvn verify -P integration-test -Dtest=BlakenTest -DfailIfNoTests=false"
                    }
                }
            }
        }

        stage('Acceptance tests') {
            steps {
                withEnv(['JAVA_HOME=/usr/lib/jvm/java-11-openjdk']) {
                    withMaven(maven: 'Maven 3.5') {
                        sh "mvn verify -P acceptance-test -Dtest=BlakenTest -DfailIfNoTests=false"
                    }
                }
            }
        }

        stage('Sonar') {
            steps {
                withCredentials([string(credentialsId: 'sonarqube-ibaieus-token', variable: 'SONAR_TOKEN')]) {
                    configFileProvider([configFile(fileId: 'urlshortener-env', variable: 'ENV_FILE')]) {
                        load "${ENV_FILE}"
                        sh 'mvn sonar:sonar -DskipTaskScanner=true -Dsonar.login=${SONAR_TOKEN}  -Dsonar.branch.name=${BRANCH_NAME} -Dsonar.host.url=${SONAR_URL} --file pom.xml'
                    }
                }
            }
        }

        stage('Package JAR') {
            steps {
                withEnv(['JAVA_HOME=/usr/lib/jvm/java-11-openjdk']) {
                    withMaven(maven: 'Maven 3.5') {
                        sh "mvn package spring-boot:repackage -DskipTests"
                    }
                }
            }
        }

        stage('Build docker image') {
            steps {
                script {
                    dockerImage = docker.build DOCKER_REGISTRY + ":$BUILD_NUMBER"
                }
            }
        }

        stage('Snyk dependencies') {
            steps {
                withMaven(maven: 'Maven 3.5') {
                    snykSecurity(
                        snykInstallation: 'snyk-latest',
                        snykTokenId: 'snyk-ibaieus',
                        organisation: 'ibai.eus',
                        projectName: 'url-shortener',
                        monitorProjectOnBuild: "${GIT_BRANCH}" == 'master',
                        failOnIssues: true,
                        failOnError: true,
                        severity: 'low'
                    )
                }
            }
        }

        stage('Snyk container') {
            steps {
                snykSecurity(
                    snykInstallation: 'snyk-latest',
                    snykTokenId: 'snyk-ibaieus',
                    organisation: 'ibai.eus',
                    projectName: 'url-shortener',
                    monitorProjectOnBuild: "${GIT_BRANCH}" == 'master',
                    failOnIssues: true,
                    failOnError: true,
                    severity: 'low',
                    additionalArguments: "-d --docker --file=Dockerfile ${DOCKER_REGISTRY}:${BUILD_NUMBER}"
                )
            }
        }

        stage('Release docker image') {
            when {
                expression { BRANCH == "master" }
            }
            steps{
                script {
                    docker.withRegistry('', DOCKER_REGISTRY_CREDENTIAL) {
                        dockerImage.push()
                    }
                }
            }
        }

        stage('Deploy') {
            when {
                expression { BRANCH == "master" }
            }
            steps{
                configFileProvider([configFile(fileId: 'urlshortener-env', variable: 'ENV_FILE')]) {
                    load "${ENV_FILE}"
                    sh '''
                        cd .kubernetes/manifests

                        cat secrets.yml.enc | base64 --decode > secrets.yml.blob
                        aws kms decrypt --region ${AWS_REGION} \
                            --ciphertext-blob fileb://secrets.yml.blob \
                            --output text \
                            --query Plaintext | base64 --decode > secrets.yml
                        kubectl apply -f secrets.yml

                        export VERSION="${BUILD_NUMBER}"
                        export PROFILE=prod
                        export NODE="${NODE}"
                        export DOMAIN="${DOMAIN}"
                        envsubst < main.yml | kubectl apply -f -
                        kubectl rollout status -n urlshortener deployment/urlshortener
                    '''
                }
            }
        }
    }
}
