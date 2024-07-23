## BC Courts Document Download API

## Overview  

The Courts Document Download API provides a set of operations for requesting the pre-download of Application Records from both Civil and Criminal SCV Court List Views to a user's OneDrive.


## Components 

The components involved in the document transfer process include: 

| Component              | Repo Path        | Description |
| ------------------ | --------------------- |------------
| DocDownloaderAPI | src/backend/DocDownloaderAPI | Main API |
| DocDownloaderRedisCache | src/backend/DocDownloaderRedisCache | Redis Cache Client API |
| Redis Cache Server  | None			| Installed via Docker | 


See the ReadMe.MD file for each component for a complete description of configuration and usage. 

## Pre Download Theory of Operation
 
The DocDownloaderAPI receives a request and immediately responds to the caller with a 'transferId' while concurrently initiating a new job (thread) to carry out the following tasks:  
* Call for the file to be 'pushed' from the Object store to the intermediate NFS storage via ORDS. 
* Update the Redis Client after a successful call to push. 
* Commence polling of the S3 storage for the arrival of the expected file. Note: A background process moves any new files written to the NFS to the S3 exchange bucket. On a succesful write to the S3 storage, the file is deleted from the NFS.
* Once acknowledgement is receive that the file has landed in the S3 storage, a new upload session is created with the MS Graph API which facilittate s the movement of the file, in chunks between the S3 storage bucket and a user's OneDRive location using streams to keep the memory requirement low. After each successful push of a file chunk, the Redis Client is informed. 
* Once the requested file arrives at the OneDrive location, a final call is made to the S3 storage to delete the file and completing the process. 
* At any time during the above processing, the application requesting the file may request the file transfer status which returns with a percentage complete or error state.  









 
