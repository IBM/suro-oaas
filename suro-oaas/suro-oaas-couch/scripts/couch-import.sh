#! /usr/bin/env bash

# PARSING PARAMETERS
# ------------------------------------------------------

	usage() { echo "Usage: $0 [-u <username>] [-p <password>] [-h <host>] [-db <db-name>] [-d <data-folder>]" 1>&2; exit 1; }

	while [[ $# > 1 ]]; do
		key="$1"
		case $key in
			-user)
				USERNAME="$2"
				shift # past argument
				;;
			-password)
				PASSWORD="$2"
				shift # past argument
				;;
			-host)
				HOST="$2"
				shift # past argument
				;;
			-port)
				PORT="$2"
				shift # past argument
				;;
			-db)
				DBNAME="$2"
				shift # past argument
				;;
			-data)
				DATA_FOLDER="$2"
				shift # past argument
				;;
			*)
				usage
			;;
		esac
		shift # past argument or value
    done


# PARAMETER FALLBACK CONFIGURATION
# ------------------------------------------------------

	## hard-coded fallbacks
	HOST=${HOST:-localhost}
	PORT=${PORT:-5984}
	DBNAME=${DBNAME:-suro}
	DATA_FOLDER=${DATA_FOLDER:-../../suro-oaas-demo/database/couch/cloudant-demo-data-0.0.1}

	## computed variables
	DESIGN_FOLDER=$DATA_FOLDER/_design

	## handle invalid parameter combinations
	if ([ -z "$USERNAME" ] && [ -n "$PASSWORD" ]) || ([ -n "$USERNAME" ] && [ -z "$PASSWORD" ])
		then
			echo "If you specify either username or password, you need to specify both."
			exit 2
	fi

	## compute connect string
	CONNECT_STRING="http://$USERNAME:$PASSWORD@$HOST:$PORT/$DBNAME"
	if [ -z "$USERNAME" ] && [ -z "$PASSWORD" ]; then
		# handle without username/password
		CONNECT_STRING="http://$HOST:$PORT/$DBNAME"
	fi


	## debug output
	printf "Using CouchDB: $CONNECT_STRING\n\n"
	echo "Searching folder $DATA_FOLDER ..."



# IMPORT DATA
# ------------------------------------------------------

	# for each file in data folder import the content:
	for f in `ls $DATA_FOLDER`
	do
		# TODO: check if _design folder is processed here as well

	  	DOC_PATH=$DATA_FOLDER/$f

		# Debug code:
	  	# echo "$DOC_PATH"
	  	# cat "$DOC_PATH"

		# make sure to not import index.json
	  	if [ $f != "index.json" ]
		then
		 	# import document
		  	curl -X POST -d "@$DOC_PATH" -H "Content-Type: application/json" $CONNECT_STRING
		  	P=$f
	  	fi
	done

# IMPORT DESIGNS
# ------------------------------------------------------

	# for each file in the data/_design sub-folder import the content as a design document
	for d in `ls $DESIGN_FOLDER`
	do
	  	DESIGN_PATH=$DESIGN_FOLDER/$d

	  	# echo "$DESIGN_PATH"

		# cut of the file extension to determine the design name
	  	DESIGN_DOC=`echo "$d" | cut -d'.' -f1`

	  	# echo "$DESIGN_DOC"

		# import design document
	  	curl -X PUT $CONNECT_STRING/_design/$DESIGN_DOC -d @$DESIGN_PATH
	done

