# NanoSat MO Framework - Mission: Raspberry Pi
This repository contains the implementation of the NanoSat MO Framework to run on a Raspberry Pi.
The NMF on the Raspberry Pi can be configured to run with or without a simulator attached to it. The simulator provides most of the platform functionalities accessible to the experimenter. The simulator allows running NMF applications without an access to a real satellite hardware.

The project must be compiled in the development machine and will be transfered to the Raspberry Pi (testing machine) via scp during compilation. Then, one must install it, and run it.
The file transfer is directly part of the compilation in order to facilitate the delivery of the files to the Raspberry Pi during the development process: **Develop, Compile, Run!**

The official website of the NanoSat MO Framework is available [here].

## Why should you install the NMF on a Raspberry Pi?

The NanoSat MO Framework is expected to run on advanced on-board computers available on the latest satellites. These new satellites typically have ARM architectures with Linux-based Operating Systems, usually built with the Yocto project.

The installation of the NMF on a Raspberry Pi provides a cheap testbed environment for trying out the software. This is particular useful because of the similar characteristics of the Raspberry Pi to the cutting-edge ESA radiation-hardened on-board computers flying in space. Development and debugging can be quickly performed.

# Configuring the Raspberry Pi

The build copies the NMF to the Raspberry Pi over SSH, as root, into `/nanosat-mo-framework`.
Use an SSH key for this copy, not a password:
root then has no password, and nobody can log in as root over SSH with a password.

1. Enable SSH on the Raspberry Pi: `sudo raspi-config` (Interface Options -> SSH).

2. On the development machine, create a key if you do not have one yet: `ssh-keygen -t ed25519`.

3. Allow the key to log in as root on the Raspberry Pi.
   Copy the public key to your own user on the Raspberry Pi, then append it to root's keys:
   ```
   ssh-copy-id <user>@<raspberry-pi>
   ssh <user>@<raspberry-pi> 'sudo install -d -m 700 /root/.ssh && cat ~/.ssh/authorized_keys | sudo tee -a /root/.ssh/authorized_keys > /dev/null'
   ```
   Do not change `PermitRootLogin` in `/etc/ssh/sshd_config`:
   its default, `prohibit-password`, already lets root log in with a key and refuses root's password.

4. Check that `ssh root@<raspberry-pi>` logs in without asking for a password.

# Building the project

1. Build the NanoSat MO Framework from the root of this repository, as described in its [README](../README.md).

2. Build this mission and copy it to the Raspberry Pi with the `deploy-on-rpi` profile:
   ```
   mvn install -Pdeploy-on-rpi -Ddeploy.remote.url=scp://root@<raspberry-pi>/
   ```
   * Use `scpexe://` instead of `scp://` to use your system's `ssh` instead of the Java SSH library.
     It also uses your `ssh-agent`.
     This is recommended on Linux and Mac.

# Installing the NMF on your Raspberry Pi and running it

1. Install a Java 21 runtime on the Raspberry Pi, if it does not have one yet.
   Raspberry Pi OS 12 (Bookworm) packages only Java 17,
   so install, for example, Eclipse Temurin 21 from https://adoptium.net/installation/linux/.

2. Provision the NMF, once, from the `/nanosat-mo-framework` folder:
   ```
   cd /nanosat-mo-framework/
   sudo chmod +x setup_linux_userspace.sh
   sudo ./setup_linux_userspace.sh
   ```
   The script creates the `nmf-admin` account that the Supervisor runs as.
   The account has no password and is locked, so nobody can log in as it.
   It also installs the `sudo` rules that the Supervisor needs to create the accounts of the Apps.

   **Warning**: If you get the error "sudo: unable to execute ./setup_linux_userspace.sh: No such file or directory",
   it is because the file was generated on a Windows machine and has CRLF line endings.
   Fix it with:
   ```
   sudo apt-get install dos2unix
   sudo dos2unix setup_linux_userspace.sh
   sudo ./setup_linux_userspace.sh
   ```

3. The script should print the message "Success! The NanoSat MO Framework was set up for linux-userspace isolation!".
   Otherwise, read the error and fix it accordingly.

4. Start the NanoSat MO Supervisor as `nmf-admin`, not as root
   (the Supervisor refuses to run as root):
   ```
   cd /nanosat-mo-framework/
   sudo -u nmf-admin ./start_supervisor.sh
   ```

# Bugs Reporting
Bug Reports can be submitted on: [Bug Reports]

Or directly in the respective source code repository.

# License
This mission is part of the NanoSat MO Framework and is **licensed** under:

**[European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4](https://github.com/esa/nanosat-mo-framework/blob/master/LICENCE.md)**.

	
[NMFImage]: http://nanosat-mo-framework.github.io/img/NMF_logo_1124_63.png
[NanoSat MO Framework]: https://nanosat-mo-framework.github.io/
[here]: https://nanosat-mo-framework.github.io/
[GitHub]: https://github.com/esa/nanosat-mo-framework
[Bug Reports]: https://github.com/esa/nanosat-mo-framework/issues
