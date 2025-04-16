FROM docker.elastic.co/elasticsearch/elasticsearch:8.17.4

# Nori 플러그인 설치
RUN elasticsearch-plugin install analysis-nori