#!/bin/sh
./bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic user_profile --partitions 1 --replication-factor 1
./bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic tracks --partitions 1 --replication-factor 1
kafka-topics --zookeeper localhost:2181 --create --topic user_genres --partitions 1 --replication-factor 1
kafka-topics --zookeeper localhost:2181 --create --topic user_locations --partitions 1 --replication-factor 1
kafka-topics --zookeeper localhost:2181 --create --topic location_weathers --partitions 1 --replication-factor 1
kafka-topics --zookeeper localhost:2181 --create --topic user_weather --partitions 1 --replication-factor 1
kafka-topics --zookeeper localhost:2181 --create --topic recommendations --partitions 1 --replication-factor 1
./bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic user_tracks_by_genre --partitions 1 --replication-factor 1
./bin/kafka-topics.sh --zookeeper localhost:2181 --create --topic genre-trackId --partitions 1 --replication-factor 1
