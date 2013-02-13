About
======

`Nuptse` is a graphical wrapper around `mount` for the Android platform. It allows to mount SD cards formatted with filesystems that Android doesn't auto mount without resorting to a terminal. 

So far it allows to specify a device, a mount point and a filesystem type. These settings are then used to mount or unmount the specified device.

`Nuptse` is pretty much alpha software. Feel free to expand it to your own needs.

Future Features
===============

 * It would be like totally awesome if `nuptse` supported [UMS](http://en.wikipedia.org/wiki/USB_mass_storage_device_class)
 * It might be nice if `nuptse` supported configurations for multiple devices

Issues
======

 * So far `nuptse` checks the current status upon redraw (turning of your device, returning from background, ...) or upon explicit demand. It would be nice if it got notified upon events that are concerned with external storage.
 * Since Google for some reason decided that external storage is evil, there is no elegant API I could find to work with such devices. No automatic detection, no checking of status, ...
