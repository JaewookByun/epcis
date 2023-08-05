# Oliot EPCIS 2.0
Version: _oliot-epcis-2.2.0_

_Note: change branch to oliot-epcis-2.1.0 for using the latest release_

An international standard, GS1 EPCIS, has enabled standardized information integration and sharing for supply chains and logistics, and  takes a new turn in the era of the Internet of Things by ratifying the major release v2.0 since the last ratification in 2016.  The official support for sensor data and Semantic Web technologies of the release as well as the legacy of GS1 would be the cornerstone for information systems that seek interoperability of data in the era. 

Auto-ID Labs., an association of global laboratories that firstly coins the concept of the Internet of Things, closely cooperate with GS1 in leading prior research. [Auto-ID Labs. Korea](https://autoidlab.kaist.ac.kr/) at Sejong University develops and maintains open-sources for GS1 standards. Oliot EPCIS has been developed and maintained since 2014 and now initiates the second generation of the system. The second generation is developed from scratch again with the standard development process called GSMP EPCIS/CBV 2.0 MSWG.

## Open Service 

oliot-epcis-2.1.0 runs and be available on a server at DFPL Lab. at Sejong University [DFPL Lab.](https://sites.google.com/view/jack-dfpl/home)
You can access the server [HERE](http://dfpl.sejong.ac.kr/epcis/home/index.html).

## How to use 'as a user'
In oliot-epcis-2.2.0.zip, there would be a single runnable file, its json configuration file and the 'schema' folder containing schema files. 
It would be available when v2.2.0 is released. But, you can export the system to a runnable jar file from org.oliot.epcis.server.EPCISServer.java

The command would be 

### epcis.jar 
```bash
> java -jar epcis.jar configuration.json
```

## Organizations and Projects disclosing their usage

We are interested in knowing how Oliot EPCIS can be used for different application domains. Thus, if you are to use Oliot EPCIS and can disclose yourself, please send me (Jaewook Byun) your affiliation/organization information (e.g., logo, purpose of use, project name, etc.)

- Auto-ID Labs, KAIST [Link](http://autoidlab.kaist.ac.kr/)
- BIBA - Bremer Institut für Produktion und Logistik GmbH (an affiliate institute of the University of Bremen, Germany) [Link](http://www.biba.uni-bremen.de/en.html)
- IoF 2020 (EU Horizon 2020 Project) [Link](https://www.iof2020.eu/)
- NIMBLE (EU Horizon 2020 Project) [Link](https://www.nimble-project.org/)
- NIRA [Link](https://www.nira-inc.com/technologies)

## Paper Publication
- Ahn, Jaehyun, Haifa Gaza, Juhyeok Lee, Hyeongchan Kim, and Jaewook Byun, "Oliot EPCIS: an open-source EPCIS 2.0 system for supply chain transparency" SoftwareX, Elsevier  (**2023**) 101477 [PAPER](https://www.sciencedirect.com/science/article/pii/S2352711023001735?via%3Dihub)
- Ahn, Jaehyun, Haifa Gaza, Jincheol Oh, Klaus Fuchs, Jing Wu, Simon Mayer, and Jaewook Byun. "MR-FoodCoach: Enabling a convenience store on mixed reality space for healthier purchases." 2022 IEEE International Symposium on Mixed and Augmented Reality Adjunct (ISMAR-Adjunct) DEMO Session. IEEE, **2022**. [PAPER](https://ieeexplore.ieee.org/document/9974511) [DEMO](https://www.youtube.com/watch?v=iUieI7GIw2w)
- Jaewook Byun, and Daeyoung Kim. "Object traceability graph: Applying temporal graph traversals for efficient object traceability." Expert Systems with Applications 150 (**2020**): 113287. [PAPER](https://www.sciencedirect.com/science/article/pii/S0957417420301123).
- Jaewook Byun, Sungpil Woo, Yalew Tolcha, and Daeyoung Kim. "Oliot EPCIS: Engineering a Web information system complying with EPC Information Services standard towards the Internet of Things." Computers in Industry 94 (2018): 82-97. [PAPER](https://www.sciencedirect.com/science/article/pii/S016636151730458X).
- Jaewook Byun, Daeyoung Kim. "Oliot EPCIS: New EPC information service and challenges towards the Internet of Things." RFID (RFID), 2015 IEEE International Conference on. IEEE, **2015**. [PAPER](http://ieeexplore.ieee.org/xpls/abs_all.jsp?arnumber=7113075&tag=1)
- Jaewook Byun, Sungpil Woo, Daeyoung Kim,"Efficient and privacy-enhanced object traceability based on unified and linked EPCIS events." Computers in Industry 89 (**2017**): 35-49. [PAPER](http://www.sciencedirect.com/science/article/pii/S016636151630135X)
- Jaewook Byun, Daeyoung Kim, "EPC Graph Information Service: Enhanced object traceability on unified and linked EPCIS events," The 16th International Conference on Web Information System Engineering (WISE 2015), Miami, Florida, USA, November 1-3, **2015**. [PAPER](http://link.springer.com/chapter/10.1007/978-3-319-26190-4_16)

Note: if you publish papers using Oliot EPCIS and want to disclose it, let us know.

## Specification 

- EPCIS 2.0
- Java, Web Service (JDK 20, Eclipse Vert.X)
- MongoDB 6.x

## Contact

Jaewook Byun, Ph.D. 

Assistant Professor, Department of Software, Sejong University, Republic of Korea

Associate Director, Auto-ID Labs, Korea

- email: <jwbyun@sejong.ac.kr>, <bjw0829@gmail.com>
- Lab. homepage: <https://sites.google.com/view/jack-dfpl/home>
- YouTube: <https://www.youtube.com/@bjw0829>
