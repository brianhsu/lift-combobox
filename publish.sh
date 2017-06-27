#!/usr/bin/env bash

if [ $# -eq 0 ]; then
  PUBLISH=publishLocal
else
  PUBLISH=$1
fi

publish() {
  LIFT_VERSION="set liftVersion in ThisBuild := \"$1\""
  CROSS_SCALA="set crossScalaVersions := Seq($2)"

  sbt "$LIFT_VERSION" "$CROSS_SCALA" clean "+ update" "+ test" "+ $PUBLISH"
}

publish "3.1.0-RC1" '"2.11.11", "2.12.2"'
publish "3.0.1" '"2.11.11", "2.12.2"'
