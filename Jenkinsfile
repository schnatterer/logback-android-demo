#!groovy

node { // No specific label

    javaHome = tool 'JDK8-u101'

    catchError {
        stage('Checkout') {
            git 'https://github.com/schnatterer/logback-android-demo'
        }

        stage('Build') {
            gradle 'clean check assembleRelease'
            archive '**/build/*.apk'
        }
    }
    step([$class: 'Mailer', recipients: 'johannes@schnatterer.info', notifyEveryUnstableBuild: true, sendToIndividuals: true])
}

def javaHome

def gradle(def args) {
    withEnv(["JAVA_HOME=${javaHome}", "PATH=${javaHome}/bin"+':$PATH']) {
        sh "chmod +x gradlew"
        sh "./gradlew -version"
        sh "./gradlew ${args} --stacktrace"
    }
}
