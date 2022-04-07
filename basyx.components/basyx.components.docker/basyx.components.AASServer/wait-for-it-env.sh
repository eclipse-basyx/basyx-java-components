#!/bin/bash
for row in $(echo "${WAITING_FOR}" | jq -r '.[] | @base64'); do
    _jq() {
     echo ${row} | base64 -d | jq -r ${1}
    }
    /bin/bash -c "./wait-for-it.sh -h $(_jq '.host') -p $(_jq '.port') -t $(_jq '.timeout')"
done
