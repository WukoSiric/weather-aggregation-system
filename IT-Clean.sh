#!/bin/bash
#Removes make[1] lines from log files that occurs when using make to run integration tests
sed -i '/^make/d' ./Tests/*.log
sed -i '/^make/d' ./Tests/*.log.comp