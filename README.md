# Shades of Mort'ton

A RuneLite plugin for the Shades of Mort'ton minigame.

Interacting with the burial site (funeral pyre) is tick-sensitive — spam-clicking
re-issues the interaction every click, so the character keeps restarting and the action
never completes. This plugin **blocks redundant clicks on the burial site while the
action is already in progress**, so you can spam-click and the first click still resolves
cleanly.

## Config

- **Debug logging** — logs every menu click and animation change. Used to capture the
  burial-site object IDs; leave off during normal play.
