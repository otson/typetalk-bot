# Typetalk Bot

Bot application for Typetalk written in Kotlin using Spring Boot.
Listens to incoming webhook events from Typetalk and Backlog.
User can interact with the bot with following commands by typing in the chat:

## Typetalk webhook

* **!hello**: Responds with a greeting.
* **!today**: Responds with a list of anime that will air the next 24 hours.
* **!sub `anime_id`**: Subscribe to notifications when a new episode of the specified anime is about to air.
* **!unsub `anime_id`**: Remove existing notification subscription.
* **!listsubs**: Responds with list of anime ids of current own subscriptions.

## Backlog webhook

* Notification is sent to Typetalk of created issues.

## Running app

* (optional) `TYPETALK_TOKEN` and `TYPETALK_API_URL` environment variables should be set in order for the bot to be able
 to send Backlog event and notification messages.
    * Create a new Typetalk bot, copy its `Typetalk token` and `Get or post messages URL`.
* Run `gradlew bootRun -DTYPETALK_TOKEN=token_here -DTYPETALK_API_URL=url_here`, or just `gradlew bootRun`.

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
    * Make sure the bot has `topic.read` and `topic.write` permissions and is set to receive `all` messages.
5. Go to Backlog, add a new webhook `copied_address/webhook/backlog`,
    e.g. `https://4d1f250f574d.ngrok.io/webhook/backlog`.
    * `Issue Created` event should at least be enabled.
5. Webhook events from Backlog and the Typetalk topic the bot is in are now forwarded to your app running in localhost.

[1]: https://ngrok.com/