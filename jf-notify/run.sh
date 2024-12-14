#!/bin/bash
# App InsightのJAR設定があるとエラーになるため解除
unset JAVA_TOOL_OPTIONS
java -jar jf-notify.jar
