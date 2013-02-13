About
======

`Nuptse` is a graphical wrapper around `mount` for the Android platform. It
allows to mount SD cards formatted with filesystems that Android doesn't auto
mount without resorting to a terminal.

So far it allows to specify a device, a mount point and a filesystem type. These
settings are then used to mount or unmount the specified device.

`Nuptse` is pretty much alpha software. Feel free to expand it to your own
needs. And share your improvements! I'd be glad to receive pull requests from
you, but I really don't mind as long as you care to distribute your
contributions. Thx!

Requirements
============

 * Your kernel needs to support the filesystem you intent to use on your device.
   Cf `/proc/filesystems`. A
   [terminal](https://play.google.com/store/apps/details?id=jackpal.androidterm)
   might help with that.
 * You will probably need to have and manage root access on your device or else
   mounting might get a bit tricky.
   [SuperSU](https://play.google.com/store/apps/details?id=eu.chainfire.supersu)
   works for me.
 * In case the standard userland doesn't come with it, the `mount` command is
   provided by
   [BusyBox](https://play.google.com/store/apps/details?id=stericson.busybox).

Future Features
===============

 * It would be like totally awesome if `nuptse` supported
   [UMS](http://en.wikipedia.org/wiki/USB_mass_storage_device_class)
 * It might be nice if `nuptse` supported configurations for multiple devices

Issues
======

 * So far `nuptse` checks the current status upon redraw (turning of your
   device, returning from background, ...) or upon explicit demand. It would be
   nice if it got notified upon events that are concerned with external storage.
 * Since Google for some reason decided that external storage is evil, there is
   no elegant API I could find to work with such devices. No automatic
   detection, no checking of status, ...
