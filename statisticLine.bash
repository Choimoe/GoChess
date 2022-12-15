#!/bin/bash
line=0
function listFiles() {
	for file in $(ls $1)
	do
		if [ -d $1/$file ]; then
			listFiles $1/$file " $2"
		else
			if [ ${file##*.} == "java" ]; then
			echo ".java: $2" "$1/$file" "line:$(wc -l $1/$file | awk '{print $1}')"
			let line+=$(wc -l $1/$file | awk '{print $1}')
			fi
		fi
	done
}
listFiles $1 "."
echo "total line:$line"
