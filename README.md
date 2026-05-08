# Walking Skeleton for Vaadin Apps

The idea behind this walking skeleton is to get a minimal Vaadin application up and running that allows a user to
perform a small end-to-end function. It has minimal implementations of the most important building blocks in place,
and they are connected in the same way as they would be in a real-world application.

## Generating Walking Skeletons

The [assembly](assembly) module generates two zip-files during the `package` build phase:

- `assembly/target/walking-skeleton-empty.zip` - Example without any views
- `assembly/target/walking-skeleton-flow.zip` - Flow example view and router layout

All zip-files are generated from the same [base project](walking-skeleton), but include different files.
