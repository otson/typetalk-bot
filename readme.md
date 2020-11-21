# Typetalk Bot

Bot application for Typetalk written in Kotlin using Spring Boot. Listens to incoming webhook events. User can interact with the bot with specified commands:
* **!hello**: Responds with a greeting.
* **!today**: Responds with a list of anime that will air the next 24 hours.
* **!sub `anime_id`**: Subscribe to notifications when a new episode of the specified anime is about to air.

## Running app

* Run `gradlew bootRun`.

## Running tests

* Run `gradlew test`.

## Webhooks in local development

When running the bot in localhost, you need to set up tunneling to localhost to be able to receive webhook events.
This can be done with **[Ngrok][1]**.

### Ngrok setup
1. Download and save the application from the [Ngrok homepage][1].
2. Run `ngrok http 8080` (or the port where the app is running in localhost).
3. Copy the displayed HTTPS forwarding address (e.g. `https://4d1f250f574d.ngrok.io`). You can also go to 
    http://localhost:4040/inspect/http to see it.
4. Go to Typetalk, add a new bot with webhook `copied_address/webhook/typetalk`,
    e.g. `https://4d1f250f574d.ngrok.io/webhook/typetalk`. 
5. Make sure the bot has `topic.read` and `topic.write` permissions and is set to receive `all` messages.
5. Webhook events from the topic the bot is in are now forwarded to your app running in localhost.

[1]: https://ngrok.com/