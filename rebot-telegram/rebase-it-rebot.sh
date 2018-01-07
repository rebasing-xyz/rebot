#!/bin/bash

# The MIT License (MIT)
#
# Copyright (c) 2017 Rebase.it ReBot <just@rebase.it>
#
# Permission is hereby granted, free of charge, to any person obtaining a copy of
# this software and associated documentation files (the "Software"), to deal in
# the Software without restriction, including without limitation the rights to
# use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
# the Software, and to permit persons to whom the Software is furnished to do so,
# subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.

# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
# FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
# COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
# IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
# CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

####################################################################################
# Systemctl script for ReBot                                                       #
#                                                                                  #
####################################################################################
# Usage                                                                            #
# sudo systemctl enable|start|stop|restart rebot                                   #
#                                                                                  #
####################################################################################
# Parameters                                                                       #
# start|stop|restart                                                               #
#                                                                                  #
####################################################################################

# All the variables will come from the file /opt/bot/rebase-it-rebot.conf

VERSION="0.1-SNAPSHOT"

case $1 in
"start")
  echo "Starting rebot."
  $JAVA_HOME/bin/java -jar -Xms150m -Xmx300m -XX:MetaspaceSize=100m -Djava.io.tmpdir=/opt/bot/tmp -Dit.rebase.telegram.token=${BOT_TOKEN} -Dit.rebase.telegram.userId=${BOT_USER_ID} -Dit.rebase.telegram.chatId=${BOT_CHAT_ID} -Dit.rebase.gitbook.token=${GITBOOKS_TOKEN} telegram-bot-${VERSION}-swarm.jar &
  echo $! > /opt/bot/rebase-it-rebot.pid
  ;;
"restart")
  echo "Restarting rebot."
  $JAVA_HOME/bin/java -jar -Xms150m -Xmx300m -XX:MetaspaceSize=100m -Djava.io.tmpdir=/opt/bot/tmp -Dit.rebase.telegram.token=${BOT_TOKEN} -Dit.rebase.telegram.userId=${BOT_USER_ID} -Dit.rebase.telegram.chatId=${BOT_CHAT_ID} -Dit.rebase.gitbook.token=${GITBOOKS_TOKEN} telegram-bot-${VERSION}-swarm.jar &
  echo $! >  /opt/bot/rebase-it-rebot.pid
  ;;
"stop")
  echo "Stopping rebot."
  kill -15 `cat /opt/bot/rebase-it-rebot.pid`
  rm -rf  /opt/bot/rebase-it-rebot.pid
  ;;
*)
  echo "Parameter not valid, use start, restart or stop."
  ;;
esac
