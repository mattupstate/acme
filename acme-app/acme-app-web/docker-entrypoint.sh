#!/bin/sh

npx ng serve --host 0.0.0.0 --disable-host-check || cat /root/.npm/_logs/*.log