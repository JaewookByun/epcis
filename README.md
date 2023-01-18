## Oliot EPCIS Second Generation ##

EPC Information Service (EPCIS) enables to capture and share standardized event/master data ratified by GS1. As an Auto-ID Labs. member [Auto-ID Labs.](https://autoidlab.kaist.ac.kr/people.html), we have been developed an open-source implementation of the standard since 2014. Oliot EPCIS v2.* has been developed from scratch again for high performance and retaining core functionality. The initial prototype is already implemented and available on a server at DFPL Lab. at Sejong University [DFPL Lab.] (https://sites.google.com/view/jack-dfpl/home). The system consists of several independent modules and we are in a finalizing step. The available module(s) is(are) as follows:

* **epcis-capture-xml**: The server implements a capture interface for Data Capture Applications (DCAs) to store EPCISDocument in a XML format into a persistent storage. 
* **epcis-query-soap**: The server implements a (part of) SOAP query interface for EPCIS Accessing Applications (EAAs) to retrieve events/master data in order to build applications. 
* **epcis-subscribe-soap**:The server implements a (part of) SOAP query interface for EPCIS Accessing Applications (EAAs) to subscribe an EPCIS system in order to deliver  events/master data periodically or on-demand.
* **epcis-capture-json**: The server implements a capture interface for Data Capture Applications (DCAs) to store EPCISDocument in a JSON format into a persistent storage.
* **epcis-query-rest**: The server implements a REST query interface for EPCIS Accessing Applications (EAAs) to retrieve events/master data in order to build applications or ubscribe an EPCIS system in order to deliver  events/master data periodically or on-demand. 

## Specification ##

* **Language**: Open JDK 19
* **Application Framework**: Eclipse Vert.x v4.3.7
* **Backend Storage**: MongoDB v6.0.3

## Open Server ##

* Access [Server](http://dfpl.sejong.ac.kr/epcis/home/index.html)

## Organizations and Projects disclosing their usage ##

Auto-ID Labs, Korea is interested in knowing how Oliot EPCIS can be used for different application domains. Thus, if you are to use Oliot EPCIS and can disclose yourself, please send me (Jaewook Byun <jwbyun@sejong.ac.kr>) your affiliation/organization information (e.g., logo, purpose of use, project name, etc.) as follows. 

* Auto-ID Labs, KAIST [Link](http://autoidlab.kaist.ac.kr/)

* BIBA - Bremer Institut für Produktion und Logistik GmbH (an affiliate institute of the University of Bremen, Germany) [Link](http://www.biba.uni-bremen.de/en.html)

* IoF 2020 (EU Horizon 2020 Project) [Link](https://www.iof2020.eu/)

* NIMBLE (EU Horizon 2020 Project) [Link](https://www.nimble-project.org/)

* NIRA [Link](https://www.nira-inc.com/technologies)

## Paper Publication* ##

- Jaewook Byun, and Daeyoung Kim. "Object traceability graph: Applying temporal graph traversals for efficient object traceability." Expert Systems with Applications 150 (2020): 113287. [URL](https://www.sciencedirect.com/science/article/pii/S0957417420301123).

- Jaewook Byun, Sungpil Woo, Yalew Tolcha, and Daeyoung Kim. "Oliot EPCIS: Engineering a Web information system complying with EPC Information Services standard towards the Internet of Things." Computers in Industry 94 (2018): 82-97. [URL](https://www.sciencedirect.com/science/article/pii/S016636151730458X).

- Jaewook Byun, Daeyoung Kim. "Oliot EPCIS: New EPC information service and challenges towards the Internet of Things." RFID (RFID), 2015 IEEE International Conference on. IEEE, 2015. [URL](http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=7113075&tag=1)

- Jaewook Byun, Sungpil Woo, Daeyoung Kim,"Efficient and privacy-enhanced object traceability based on unified and linked EPCIS events." Computers in Industry 89 (2017): 35-49. [URL](http://www.sciencedirect.com/science/article/pii/S016636151630135X)

- Jaewook Byun, Daeyoung Kim, "EPC Graph Information Service: Enhanced object traceability on unified and linked EPCIS events," The 16th International Conference on Web Information System Engineering (WISE 2015), Miami, Florida, USA, November 1-3, 2015. [URL](http://link.springer.com/chapter/10.1007/978-3-319-26190-4_16)

## Contact ##

* Jaewook Byun, Ph.D.
* Assistant Professor, Sejong University, Republic of Korea [Link](https://sejong.elsevierpure.com/en/persons/jaewook-byun)
* Associate Director, Auto-ID Labs. Korea [Link](http://autoidlab.kaist.ac.kr/)
* YouTube [Link](https://www.youtube.com/channel/UC988e-Y8nto0LXVae0aqaOQ)
