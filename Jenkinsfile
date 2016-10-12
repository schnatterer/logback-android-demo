#!groovy

node { // No specific label

    javaHome = tool 'JDK8-u101'

    catchError {
        stage('Checkout') {
            git 'https://github.com/schnatterer/logback-android-demo'
        }

        stage('Assemble') {
            gradle '-version'
            // Must be assembled first, because the unit tests/linting relies on some files (e.g. assets) copied during assemble
            gradle 'clean assembleRelease'
            // Archive APK
            archive '**/*.apk'
        }

        stage('Check') {
            gradle 'lintRelease testReleaseUnitTest'
            // Archive Lint reports
            archive '**/build/outputs/lint-results-*.html'
        }
    }
    // Archive JUnit results, if any
    junit allowEmptyResults: true, testResults: '**/build/test-results/**/*.xml'
    // Send mail on failure
    step([$class: 'Mailer', recipients: '$RECIPIENTS', notifyEveryUnstableBuild: true, sendToIndividuals: true])
}

def javaHome

def gradle(def args) {
    withEnv(["JAVA_HOME=${javaHome}", "PATH=${javaHome}/bin"+':$PATH']) {
        sh "chmod +x gradlew"
        sh "./gradlew ${args} --stacktrace"
    }
}
