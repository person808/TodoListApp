# TodoList App

Created by Kainalu Hagiwara and Ahmaad Idrees for CS112 final project.

Screenshots: https://imgur.com/a/EaXqzUK

Requires a Firebase Database setup with these rules: 

```json
{
  "rules": {
    "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid"
    }
  }
}
```