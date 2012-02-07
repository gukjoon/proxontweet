# A Prox On Tweet

## Install
### get it
git clone _

### play framework
wget http://download.playframework.org/releases/play-1.2.4.zip
unzip play-1.2.4.zip
mv play-1.2.4 /Applications/
export PATH=/Applications/play-1.2.4:$PATH

### dependencies
play install scala
play install oauth
play dependencies --sync

## Run/Deploy

### Testing
play test

### Dev Env
play run

## API
/twitter-search/status?q=
	returns status response

Status Response
	{ twitter_response_time : "",
	  query : "",
	  results : [StatusResult]}       

StatusResult
	{ from_user : "",
	  text : ""}


## A few notes

### Design choices
1. Optimized for readability as opposed to speed.
2. Commenting assumes the reader is literate in Scala and the Play Framework
3. Will not search for users as it requires OAuth. Since the consumer of this data is presumably another program, the requirement of using a browser to do OAuth does not fit with the requirements.