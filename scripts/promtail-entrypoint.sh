#!/bin/sh

# 로그 디렉토리 없으면 생성
mkdir -p /logs

# promtail 실행
promtail -config.file=/etc/promtail/config.yml
