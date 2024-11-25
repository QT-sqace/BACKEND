node {
    environment {
        CI = 'false'
        DOCKER_IMAGE = 'hajewoong/qt-sqace-backend'
        PATH = "$PATH:$HOME/bin"
        JAVA_HOME = "/usr/lib/jvm/java-17-openjdk-17.0.13.0.11-3.0.1.el8.x86_64"
    }
    stage('Checkout') {
        step {
            script {
                def branchName = env.BRANCH_NAME
                if (branchName == 'gateway') {
                    dir('gateway') {
                        def gatewayPipeline = load 'Jenkinsfile'
                        gatewayPipeline.runGatewayPipeline()
                    }
                } else if (branchName == 'team-service') {
                    dir('team-service') {
                        load 'Jenkinsfile'
                    }
                } else {
                    echo "No specific Jenkinsfile for this branch"
                }
            }
        }
    }
}
