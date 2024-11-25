node {
    def branchName = env.BRANCH_NAME
    if (branchName == 'gateway') {
        dir('gateway') {
            load 'Jenkinsfile'
        }
    } else if (branchName == 'team-service') {
        dir('team-service') {
            echo 'ls'
            load 'team-service/Jenkinsfile'
        }
    } else {

        echo "No specific Jenkinsfile for this branch"
    }
}