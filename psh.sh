#!/bin/bash
rm /home/tookuk/workspace/tookukHighloadCup/hlc.jar
cp /home/tookuk/workspace/tookukHighloadCup/target/tookukHighloadCup-1.0-SNAPSHOT-jar-with-dependencies.jar /home/tookuk/workspace/tookukHighloadCup/hlc.jar
sudo docker build --no-cache -t hooy .
sudo docker tag hooy stor.highloadcup.ru/travels/classy_butterfly
sudo docker push stor.highloadcup.ru/travels/classy_butterfly
