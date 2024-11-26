node {
    def branchName = env.BRANCH_NAME
    if (branchName == 'gateway') {
        dir('gateway') {
            load 'Jenkinsfile'
        }
    } else if (branchName == 'team-service') {
        dir('team-service') {
            load 'Jenkinsfile'
        }
    } else if (branchName == 'user-service'){
        dir('user-service') {
            load 'Jenkinsfile'
        }
    }

    else {

        echo "No specific Jenkinsfile for this branch"
    }
}