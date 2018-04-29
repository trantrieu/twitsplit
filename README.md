## *Project TwitSplit*

**Tweeter** is an android app that allows users to post short messages limited to 50 characters each.
Sometimes, users get excited and write messages longer than 50 characters.
Instead of rejecting these messages, we would like to add a new feature that will split the message into parts and send multiple messages on the user's behalf, all of them meeting the 50 character requirement.

## Features
* Allow the user to input and send messages.
* Display the user's messages.
* If a user's input is less than or equal to 50 characters, post it as is.
* If a user's input is greater than 50 characters, split it into chunks that each is less than or equal to 50 characters and post each chunk as a separate message.
* Messages will only be split on whitespace. If the message contains a span of non-whitespace characters longer than 50 characters, display an error.
* Split messages will have a "part indicator" appended to the beginning of each section. In the example above, the message was split into two chunks, so the part indicators read "1/2" and "2/2". Be aware that these count toward the character limit.

## Technical
* Language: Kotlin
* Architecture: MVP + Clean

## Libraries
* Dagger2
* RXJava
* Android Support Library

## Testing
* Local unit test: JUnit, Mockito
* Instrument test: Espresso
* Monkey test
