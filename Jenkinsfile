node {
    def branchName = env.BRANCH_NAME
    if (branchName == 'gateway') {
        checkout scm
        dir('gateway') {
            load 'Jenkinsfile'
        }
    } else if (branchName == 'team-service') {
        checkout scm
        dir('team-service') {
            load 'Jenkinsfile'
        }
    } else {
        echo "No specific Jenkinsfile for this branch"
    }
}