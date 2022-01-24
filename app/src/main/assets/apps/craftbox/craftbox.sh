#! /bin/bash

SCRIPT_PATH=$(realpath ${BASH_SOURCE})
sudo rm -f $SCRIPT_PATH

if [ -z "$AUTH_TOKEN" ]; then
  echo "echo 'AUTH_TOKEN not set'" > initfile
  echo "echo 'Running on local network only'" >> initfile
else
  echo "echo 'AUTH_TOKEN set'" > initfile
  echo "/usr/local/bin/ngrok authtoken $AUTH_TOKEN" >> initfile
  echo "/usr/local/bin/ngrok tcp 25565" >> initfile
fi
xterm -e /bin/bash --init-file initfile &

if [ -f minecraft_server.jar ]; then
  echo "$SHA1 minecraft_server.jar" | sha1sum -c
  if [ $? != 0 ]; then
    rm minecraft_server.jar
  fi
fi

if [ ! -f minecraft_server.jar ]; then
  wget -O minecraft_server.jar https://launcher.mojang.com/v1/objects/$SHA1/server.jar
  chmod +x minecraft_server.jar
fi

echo eula=true > eula.txt
echo "java $JAVA_MEMORY -jar minecraft_server.jar nogui"
java $JAVA_MEMORY -jar minecraft_server.jar nogui &


