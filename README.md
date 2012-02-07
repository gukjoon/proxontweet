# A Prox On Tweet #

## Install ##
### play framework ###
    wget http://download.playframework.org/releases/play-1.2.4.zip
    unzip play-1.2.4.zip
    mv play-1.2.4 /Applications/
    export PATH=/Applications/play-1.2.4:$PATH

### dependencies ##
From your proxontweet directory:

    play install scala
    play install oauth
    play dependencies --sync

## Run/Deploy ##

### Testing ###

    play test

You may then use the Play Framework's built in testing facilities at http://localhost:9000/@tests to run both the ScalaTests and Selenium tests. More on this [here](http://www.playframework.org/documentation/1.2.1/test#running)

### Dev Env ###

    play run

## API ##
/twitter-search/status?q=
### Status Response ###
	{ completed_in : "",
	  query : "",
	  results : []}

* completed_in : Twitter response time
* query : The requested query
* results : A list of StatusResult objects
	  

### StatusResult ###
	{ from_user : "",
	  text : ""}

* from_user : User that sent the tweet
* text : Text of the tweet

## Design choices ##
1. Optimized for readability as opposed to speed.
2. Commenting assumes the reader is literate in Scala and the Play Framework.
3. Will not search for users as the API call requires OAuth. The consumer of this data will be another program and OAuth is browser-based.
4. Used 404 error instead of 400 for empty query string as Play does not allow a message with a 400 response.