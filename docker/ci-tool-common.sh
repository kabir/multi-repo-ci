#!/bin/sh

############################################################
# Common code and utils for the CI tooling
############################################################

ERROR_COLOUR='\033[0;31m'
WARN_COLOUR='\033[0;33m'
RESET_COLOUR='\033[0m'

############################################################
# Functions

logWarn() {
  echo -e "${WARN_COLOUR}${1}${RESET_COLOUR}"
}

logError() {
  echo -e "${ERROR_COLOUR}${1}${RESET_COLOUR}"
}

initGit() {
  git config --global user.email "ci@example.com"
  git config --global user.name "CI Action"
}

# keep track of the last executed command
trap 'last_command=${current_command}; current_command=${BASH_COMMAND}' DEBUG
# echo an error message before exiting
trap 'echo -e "${ERROR_COLOUR}\"${last_command}\" failed with exit code $?.${RESET_COLOUR}"' EXIT


