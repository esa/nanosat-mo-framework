#!/bin/sh
cd ${0%/*}

###############################################################################
# Variables:
user_nmf_admin="nmf-admin"
user_nmf_admin_password="raspberry"
user_nmf_app_prefix="app-"
group_nmf_apps="nmf-apps"
supervisor_mainclass="esa.mo.nmf.provider.NanoSatMOSupervisorRaspberryPiImpl"
start_script_name="start_supervisor.sh"
dir_nmf="/nanosat-mo-framework/"
dir_home="/home/"
###############################################################################

# The script must be run as root
if [ $(whoami) != 'root' ]; then
	echo "The current user is: $(whoami)"
	echo "Please run this script as root or with sudo!"
	exit 1
fi

# Check if the NMF Admin User exists
if id -u "$user_nmf_admin" >/dev/null 2>&1; then
    echo "The user $user_nmf_admin already exists! Let's delete it and create again..."
	deluser $user_nmf_admin
	# todo: remove home
	RESULT=$?

	if [ $RESULT -eq 0 ]; then
	  echo "The user $user_nmf_admin was successfully deleted!"
	else
	  echo "ERROR!!! The user $user_nmf_admin could not be deleted!"
	  echo "Please close any open sessions with the $user_nmf_admin user and try again!"
	  exit 1
	fi
fi

# Add the NMF Admin User and set password
#useradd $user_nmf_admin -m -s /bin/bash --user-group
addgroup -S $user_nmf_admin
adduser -S -s /bin/sh -G $user_nmf_admin $user_nmf_admin
echo $user_nmf_admin:$user_nmf_admin_password | chpasswd

###############################################################################
# We need to allow the created user above to run the following commands:
#       1. adduser  - from: /usr/sbin/adduser
#       2. deluser  - from: /usr/sbin/deluser
#       3. su		- from: /bin/su
#       4. chmod    - from: /bin/chmod
#       5. chgrp 	- from: /bin/chgrp
#
# We can do this in 2 ways:
# 		(a) In /etc/sudoers we need to add the line:
# 	nmf-admin ALL=(ALL) NOPASSWD:/usr/sbin/adduser, ....<other commands>....
#
# 		(b) Instead of editing the sudoers file, we can create the a new file
# 	with the authorization rules in the /etc/sudoers.d directory:
# 	echo "username  ALL=(ALL) NOPASSWD:ALL" | sudo tee /etc/sudoers.d/username
#
# We will pick option (b), as it will be cleaner
###############################################################################
rule_1="/usr/sbin/adduser"
rule_2="/usr/sbin/deluser"
rule_3="/bin/su - $user_nmf_app_prefix*"
rule_4="/bin/chmod -R 770 $dir_home*, /bin/chmod -R 750 $dir_nmf*"
rule_5="/bin/chgrp"
rule_6="/usr/sbin/chpasswd"
rule_7="/usr/sbin/addgroup"
# Note: Rule 3 assumes that the Home directory is : /home/
# Rule 5 can also be: "/bin/su [!-]*, !/bin/su *root*"
# The above was obtained here: https://www.sudo.ws/man/1.8.17/sudoers.man.html
# To be removed: Rule 4
rules_text="$rule_1, $rule_2, $rule_3, $rule_4, $rule_5, $rule_6, $rule_7"
rules_path="/etc/sudoers.d/$user_nmf_admin"
rules_all="#
# This file was generated during the installation of the NanoSat MO Framework.
#
$user_nmf_admin ALL=(ALL) NOPASSWD:$rule_1
$user_nmf_admin ALL=(ALL) NOPASSWD:$rule_2
$user_nmf_admin ALL=(ALL) NOPASSWD:$rule_3
$user_nmf_admin ALL=(ALL) NOPASSWD:$rule_4
$user_nmf_admin ALL=(ALL) NOPASSWD:$rule_5
$user_nmf_admin ALL=(ALL) NOPASSWD:$rule_6
$user_nmf_admin ALL=(ALL) NOPASSWD:$rule_7
"
echo "$rules_all" | sudo tee $rules_path
chmod 440 $rules_path

# Add NMF App Group:
addgroup $group_nmf_apps

# Function to create a directory with: owner + group + permissions
create_dir(){
    permissions=$1
    owner=$2
    group=$3
    directory=$4
   
	mkdir $directory
	chown -R $owner:$group $directory
	chmod -R $permissions $directory
}

# Create the start script for the nmf: start_supervisor.sh
cat > $start_script_name <<EOF
#!/bin/sh
cd \${0%/*}
java -classpath "libs/*" $supervisor_mainclass
EOF

chown -R $user_nmf_admin:$user_nmf_admin .
chmod 775 .
chown $user_nmf_admin:$user_nmf_admin $start_script_name
chmod 700 $start_script_name

create_dir 775 $user_nmf_admin $user_nmf_admin apps
create_dir 775 $user_nmf_admin $user_nmf_admin libs
create_dir 700 $user_nmf_admin $user_nmf_admin packages
create_dir 770 $user_nmf_admin $group_nmf_apps public_square
create_dir 700 $user_nmf_admin $user_nmf_admin nmf_updates



echo "Success! The NanoSat MO Framework was installed!"





