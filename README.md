# Sell Multiplier
### Rank-based /sell multiplier for Theatria

Multiplies money earned via sell commands based on ranks.
The ranks are specified in configuration and applied via permissions.

## Permissions
```
sell.multiplier.reload: Allows player to reload configuration
sell.muliplier.iii: Example of custom permission specified in configuration.
```

## Commands
```
'/sellmultiplier reload' - Reloads the config.
    Permission required: 'sell.multiplier.reload'
```

## Config
```yaml
# Settings for /sell multipliers.
# Give multipliers to players with the permissions 'sell.multiplier.{multiplier}'
# Example: 'sell.multiplier.i', 'sell.multiplier.ii'.
# You can add as many multipliers as you want.
# Use the 'default' name to set the default multiplier that everyone has. It defaults to 1 if not set.
# If a player has more than one multiplier permission, it will pick the highest one.
sell-multipliers:
  default: 1.0 # NOT a permission. The default multiplier everyone has.
  i: 1.10
  ii: 1.20
  iii: 1.30
  iiii: 1.40
  v: 1.50

# Message sent to player when sell multiplier has been applied.
# Will not send a message for 'default', or if player possesses none of the specified permissions above.
# First %s is the (highest) permission the player has, as specified above - such as 'v'.
# Second %s is the dollar amount.
# Example: Blessing of the Oracle (V) has given you an extra ($15.00) Denarii!
message: §dBlessing of the Oracle (%s) has given you an extra §c($%s) §dDenarii!
```