#!/bin/bash

make clean
make compile
nohup java cn.hopefulme.jfortran.client.Launcher &
