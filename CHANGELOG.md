# Changelog

Short, human "what's new" notes per release -- the same text shown in the
in-app update dialog when a new version is detected. Add a new `## X.Y.Z`
section at the top before bumping `versionName` in `app/build.gradle.kts`.

Entries here are user-facing only: fixes, bugs, improvements, and features.
No internal/process notes (CI changes, release cleanup, repo housekeeping,
etc.) -- if it wouldn't mean anything to someone who just installed the app,
it doesn't belong here.

## 3.10.0-fork.6
- "Keep my clipboard" is back, and on by default: on Android 13 and newer every dictation is typed into the field like a keyboard, so whatever you copied is still there to paste afterwards (turn it off in Dictation settings to paste through the clipboard again)
- The menu's three dictation buttons are now dark grey with a grey outline instead of blue

## 3.10.0-fork.5
- Fixed "Dictation → Clipboard" putting the clipboard first: on Android 13 and newer the dictation is now typed into the field the way a keyboard does, with your clipboard pasted after it (or before it, for "Clipboard → Dictation"), and your clipboard is left untouched

## 3.10.0-fork.4
- Holding the floating button now opens a menu with three ways to dictate: "Clipboard → Dictation" and "Dictation → Clipboard" insert whatever you last copied before or after what you say, and "Dictation" works like a tap
- The menu keeps your last 10 dictations (up from 5) below the buttons

## 3.10.0-fork.3
- Fixed dictated text not appearing in the field: text is pasted again, as before 3.10.0-fork.2, and the "Keep my clipboard" setting is gone
- Hold the floating button to get any of your last 5 dictations back

## 3.10.0-fork.2
- Dictation is typed straight into the field and your clipboard is left alone, so a paste right after dictating still gives you what you copied (turn off "Keep my clipboard" to go back to pasting)
- Hold the floating button to see your last 5 dictations and tap one to insert it again (clear them in Dictation settings)

## 3.10.0-fork.1
- Cleanup no longer acts on what you dictate (like drafting the email you described) -- it keeps your words, and falls back to exactly what you said if the result doesn't match
- A short animated splash screen when the app starts (Android 12 and newer)
- Recording overlay redesigned: the logo bars turn soft red and move with your voice, instead of a blinking microphone
- Setting descriptions no longer run into their switches
- Cleaner settings screen: rounded sections, clearer switch descriptions, and a status line that tells you exactly what's missing (tap it to jump to the fix)
- Checking for updates when the app opens is now optional and off by default -- turn it on in Settings, or tap "Check for updates" any time
- The update dialog always shows what's new, with a link to the full release notes on GitHub
- Updates are checked against GitHub's published checksum before installing, and aren't installed if it doesn't match
- In local mode, audio never goes to the cloud -- if no local model is ready, you're told why instead
- Fixed a crash when the on-device speech engine couldn't be loaded
- Dictated text placed on the clipboard is marked as sensitive, so Android 13+ hides its preview
- "Copied to clipboard" now only appears when the text couldn't be inserted
- Text from the field you're dictating into is no longer written to the system log

## 3.10.0
- Refreshed the app's look with Material 3 Expressive: a richer color palette and more expressive buttons, switches, and tabs
- Smoother, springier animations across the settings screen

## 3.9.0
- Help walkthrough for Android 13+'s "Restricted settings" block, shown before opening Accessibility settings if it's not enabled yet
- The update dialog now shows what's new in the new version, not just the version number
