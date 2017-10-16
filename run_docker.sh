#!/bin/bash

/usr/local/bin/lein uberjar

/usr/bin/docker build -t edwardstx/management-ui .

/usr/bin/docker run -d --restart always -p 127.0.0.1:5007:8080 -v /etc/service:/etc/service --name management-ui edwardstx/management-ui
