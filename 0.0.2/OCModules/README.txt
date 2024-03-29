GettingStarted - This document is available on the wiki at http://code.google.com/p/opencapture.

How to get OpenCapture up and running.

Introduction

The following are simple instructions for getting OpenCatpure? up and running. This is the same information found in the README that is distributed with OC. There's a compiled version of OpenCapture included in the distribution as well as the source code. This release was compiled using jdk 1.6 update 5. Minimum java version hasn't been tested but at least 1.5 is required as generics are used.

Details

Extract to folder of choice.  The rest of filesystem is there.  

Need to change settings in the following files:

config\carrierfiles.xml - This is an example batch class.  To use change BatchClass ImagePath attribute to point to location where OpenCapture will store images in the system.

conf\log4j-ocimport.properties - There is a lo4j file for each module.  Change log4j.appender.myAppender.File setting to point to location log should be written to.  Include log file name.

conf\occognizance.properties - There is a properties file for each module.  Change the following settings in the OCCognizance and OCConverter property files:

    oc_home     - Point to location OC main folder (Zip contains OCM folder)
    log4j       - Path and name of cognizance log4j properties file.

conf\ocimport.properties - Change the following settings in the OCImport properties file:

    oc_home     - Point to location OC main folder (Zip contains OCM folder)
    log4j           - Path and name of import log4j properties file.
    archivepath - Path where original images that have been imported will be moved to.
    polldir     - Path to directory that contains images to be imported into oc.
    batchclass  - Name of batch class that images being imported will be associated with.  This is the name of the batch class xml file in the conf directory.  It will have the same batch class without spaces.  If the [BatchClass] name is Carrier Files then the BatchClass Definition XML file will be carrierfiles.xml.

    importfilesonly - set to yes to import only image files (only tiffs).
    importtrigger   - extension to trigger on.  Signifies image is ready to import.  Not functional in OC 0.0.1

Setting up database connection

Java persistence classes are used for database access. Be sure to change the persistence.xml file in the META-INF directory. This file defines the database connection properties as follows:

    <properties>
      <property name="toplink.jdbc.user" value="<dbuser name>"/>
      <property name="toplink.jdbc.password" value="<dbuser password>"/>
      <property name="toplink.jdbc.url" value="jdbc:derby://localhost:1527/OpenCapture"/>
      <property name="toplink.jdbc.driver" value="org.apache.derby.jdbc.ClientDriver"/>
      <!--<property name="toplink.ddl-generation" value="create-tables"/> -->
    </properties>

Each value should be changed according to the database system being used. In the above example the url and driver identify Derby as the database being used. You can also use other databases like MySQL, Oracle, PostgreSQL, etc.

NOTE: The first time your run an OpenCapture module, remove the comment marks (<!-- at the front and --> at the end) from the last property in the persistence.xml file. When OpenCapture is run with the create-tables property enabled, it creates the required tables. Remember, the DB itself must exist prior to using OpenCapture. This will only create the tables and not the DB. The issue with this process is that the tables are created, but the QUEUES are not setup in the QUEUES table. The queue data should be added as follows (order doesn't matter):

QUEUE_ID 	QUEUE_NAME 	QUEUE_DESC 				PLUGIN
1 		OCImport 	OC Import process
2 		OCCognizance 	OC reader process
3 		OCDelivery 	OC document delivery process
4 		OCException 	OC Exception Queue
5 		OCConverter 	Converts documents to other formats.

At present only two of the four tables are used. We included all four tables as we anticipate their use fairly quickly in new releases. We also plan to add a utility to create and setup the tables in future releases.
Creating Batch Class Definition File

See BatchClassDefinitionFile for information on the layout of the file. There is an example BatchClass file in the config directory called carrierfiles.xml. The name of the batch class definition file is derived from the batch class name itself. In the case of the sample file (carrierfiles) the name of the batch class is Carrier Files. The batch class file name should always be the batch class name minus any spaces and lower-case.

To use the sample batch class, open the carrierfiles.xml file and edit the ImagePath?. As stated in the BatchClassDefinitionFile the ImagePath? is the path where the imported or scanned images are stored. Once this is changed, the OC_DELIVERY_PATH CustomProperty? must also be changed. This particular property is used by the XMLDelivery plugin. This is where the document and it's XML index data file will be delivered. This path must exist prior to running Delivery otherwise delivery will fail. The OC_DELIVERY_FORMAT property should be set to 3 ( PDF format )as this is the only functional delivery format in the XMLDelivery plugin. The OC_WORKING_PATH is a working path where image files can be placed temporarily.

To use the sample batch class, a TIFF file named SAMPLESCAN.tif has been included in the distribution. The TIFF is located in the test directory. Place this sample TIFF in the import folder identified in the ocimport.properties file in the polldir.n setting.
Running Modules

Shell scripts have been provided to run modules for unix. CMD files for windows haven't been tested. To run module, execute the appropriate script which in the case of the sample batch class are:

runImport.sh
runCognizance.sh
runConverter.sh
runDelivery.sh

Check the BatchXML folder after each run to see how the Batch XML file changes. After the Delivery module completes successfully, the batch is deleted from OpenCapture.
Things to Know

Batch State If a batch fails in a queue the BATCH_STATE field in the BATCHES table is set to 20. Since there's currently no Batch Monitor module, manully setting this to 0 (Ready state) is the only way to reset the batch in the queue.
Lock file If a batch fails in a queue it is possible that a lock file will exist. Check the BatchXML folder in the batch's directory to see a lock file exists. If a lock file exists, delete lock file as part of the reset to ready process for the queue. To figure out the batch directory, get the BATCH_ID of the batch in question from the BATCHES table and convert to hex. The directory name is 8 characters long so look for leading zeros. Creating Separator Sheets Since only TIIFs are currently supported as an import format for OpenCapture, there are two utilities of importance that are requried if creating your own separator sheets.

First, OC converts all imported TIFFs to 200x200 DPI. To properly get the zones of the barcodes on your separator sheets you'll need to use the octf.sh (OC TIFFFormatter) utility to convert the separator sheet TIFF file to the appropriate format. Running the utility:

OCTIFFormatter - OpenCapture TIFF Formatter converts specified TIF to OC TIF format (200x200 DPI).

usage:  octf.sh <tif name> <output directory>

TIFF specified in first argument is also moved to output directory.

Next, use the ocbr.sh (OC BarcodeReader?) utility to read the zones you've identified to test reading the separator sheet. Running the utility:

./ocbr.sh /home/dnesbitt/Test/OpenCapture/00000000.tif CODE39 108 684 1488 356 1

OCBarcodeReader - Read barcode at specified zone.

usage:  ocbr.sh <tif name> <ReaderID> <X> <Y> <W> <H> [page]

Current supported Reader is CODE39 ( 3 of 9 barcode ).

NOTE: Use an imaging application like gimp or MS Photo Editor and use the selection tool to get the coordinates of the barcode zones. When selecting a zone be sure to leave a little room around the barcode.

Barcode zones are specified as:

Value 	Description
X 	Offset from left edge of separator image.
Y 	Offset from top edge of separator image.
W 	Width from X in a straight line to other side of barcode. Be sure not to cut width to edge of barcode. Allow for different value lengths.
H 	Distance from Y to bottom of barcode. Be sure not to cut height to edge of barcode. Leave a little cushion. Barcodes cannot cross multiple lines
