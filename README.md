# Network File System 

This project is a simplistic implementation of a distributed filesystem, which
will be referred to as Network File System (NFS).
While the name matches [Sun's Network File System](https://en.wikipedia.org/wiki/Network_File_System),
it is not meant to be an implementation of it.

The system is composed by three types of components: a metadata server, storage
servers and clients.

### Metadata Server
The metadata server stores the metadata of all the files and directories stored
in the NFS.
Its purposes are to provide a centralized lookup service for the files/directories
available and of which storage server holds them, and to
provide the information required by the client to be able to interact
with a storage server.
There can only be one metadata server for each NFS. (There is no replication).

### Storage Server
The purpose of a storage server is to store the directories and files of the NFS.
A storage server has a mount point in the NFS tree, and all subdirectories and
files contained in the subtree are stored in that storage server.
There can be multiple storage servers, but each with a different mount point.
(There is no replication).

A storage server is responsible for managing its subtree and keeping the
information relative to its subtree updated at the metadata server.

### Client
The client allows the end user to interact with the NFS, using a command line
interface (CLI).
The CLI can list the contents of a NFS directory, create and remove directories
and files, and transfer files.
The CLI can transfer files both ways, from the NFS to the local filesystem and
the local filesystem to the NFS, performing then the operations of getting a file
and creating a file, respectively.


## Features

### Implemented
 - Metadata server.
 - Support for multiple storage servers, each with a different mount point.
 - Client CLI (with basic error handling and input verification).
#### Operations
 - List directories, which includes information of which storage server is
   responsible by each directory.
 - Create files and directories (files are created by copying an existing file
   of the client filesystem to the NFS).
 - Retrieval of files from the NFS. (Files are printed in the console).
 - Conversion from storage server identifier to its addressing information.

### Not implemented
 - Delete files and directories.
 - Remove a storage server tree from the metadata server when the storage server
   is terminated.
 - Copy files from the NFS to the client filesystem (currently just printed at
   the console).
 - Storage server storing in and sharing files of an existing directory in the
   local filesystem. (Currently creates a temporary one).
 - Move files inside the area of control of a storage server.
 - Move files between storage servers.
 - Support paths with spaces at the client CLI.
 - Changing NFS files without the remove and create flow.


## Limitations
 - It is assumed that the execution of all operations is sequential, that is,
   the effects of one operation are completely evaluated in all the
   components of the system before the start of another operation.
 - Security concerns were not taken into consideration in the design and
   implementation.
 - Storage server mount points only at the root of the NFS tree. In practice
   this means that it is not possible to have files or directories (other than
   the storage mount points) at the root of the NFS tree.
 - Files are transferred between the client and the storage using byte[],
   which does not support very large files.
   (Using [RMIIO](https://openhms.sourceforge.io/rmiio/) would be an option to
   overcome this limitation).


## NFS as a distributed system
Viewing the NFS as a distributed system it has the following characteristics:
- _location transparency_: storage of files in multiple storage servers, but
  hides this from the user.
- _migration transparency_: transfer of files between storage servers (available
once the command _move_ is implemented).

It does not provide:
- _access transparency_: the NFS is not mounted on user OS filesystem.
- _replication_: not intended for the implementation goals.
- _concurrency transparency_: the implementation does not handle concurrency.
- _failure transparency_: not intended for the implementation goals.
