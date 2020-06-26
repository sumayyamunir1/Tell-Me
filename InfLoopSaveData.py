# -*- coding: utf-8 -*-
"""
Created on Tue Jun  9 09:24:08 2020

@author: Per-Olav
"""
import time
from opcua import Client
from datetime import datetime

#Declaring 
counter = 0
window = 30
nir = []
nirStr = []
trigger = 0
loopRunning = 0
maxLoopRunner = 0
breakLoop = 0
maxDataSize = 60



#client = Client("opc.tcp://localhost:4840/freeopcua/server/")
client = Client("opc.tcp://172.16.1.38:4850")

print('Program is started')

 

#Connecting to Apis with OPC UA
try:
    client.connect()
    
    while True:
        pStartCollect = client.get_node("ns=2;s=V|Worker_PythonIntgr.Start_DataCollect")
        qStartCollect=pStartCollect.get_value()
        ps = client.get_node("ns=2;s=V|Worker_PythonIntgr.Sekvens_Number")
        sekvensNr=ps.get_value()
        
        #Stample the time when loop starts
        if loopRunning == 0:
            today = datetime.now()
            todayStr = today.strftime("%d-%b-%Y_%H_%M")
        
        #Loop starts with command from Apis
        if (int(qStartCollect) == -1) and (breakLoop == 0):
            
            loopRunning = 1
            
            
            
            #Filling the array as input to neural network regarding the the size set by window 
            if counter < window:
                print("Filling Array - Waiting startup")
                for i in range(window):
                    p = client.get_node("ns=2;s=V|Modframe.NIR_Sum_Avarage_Apis")
                     
                    #p = client.get_node("ns=2;s=V|Replay.Logger.Modframe.NIR_Sum_Avarage_Apis")
                    q=p.get_value()
                    #print(q)
                    nir.append(q)
                    nirStr.append(str(q))
                    
                    print(nir[i])
                    #print(i)
                    time.sleep(1)
                    counter = counter+1
            #When the array is filled, the input of NN is ready to predict
            else:
                for x in range(window):
                          
                    p = client.get_node("ns=2;s=V|Modframe.NIR_Sum_Avarage_Apis")
                    #p = client.get_node("ns=2;s=V|Replay.Logger.Modframe.NIR_Sum_Avarage_Apis")
                    q=p.get_value()
                    if x < window-1:
                        nir[x] = nir[x+1]
                        nirStr[x] = nirStr[x+1]
                    else:
                        nir[x] = q
                        nirStr[x] = str(q)
                time.sleep(1)
            
            
            
            #Writing data to file
            file1 = open("C:\JavaPrograms\Dataset\PythonFinger\DataLogger\Data_sekvens_" + str(sekvensNr) + "_" + todayStr + ".csv", "a")
            file1.write(', '.join(nirStr))
            file1.write(", ")
            file1.write(str(trigger))
            file1.write("\n")
            file1.close()
            print('Writing data: ', maxLoopRunner, ' - ', breakLoop )
            
            #Controlling of max loop runner
            maxLoopRunner = maxLoopRunner + 1
            if maxLoopRunner > maxDataSize:
                breakLoop = 1
                print('Loop has reach max limit and will break')
            
        else:
            time.sleep(1)
            loopRunning = 0
            counter = 0
            maxLoopRunner = 0
            if len(nir) > 0:
                nir = [] 
                nirStr = []
                if breakLoop == 0:
                    print('Data saved with name: Data_sekvens_'+str(sekvensNr)+ '_'+ todayStr+ '.csv' )
                    print('In the following path: C:/JavaPrograms/Dataset/PythonFinger/DataLogger/')
                    print('Program waits for new start from process')
                else:
                    print('Data saved with name: Data_sekvens_'+str(sekvensNr)+ '_'+ todayStr+ '.csv' )
                    print('In the following path: C:/JavaPrograms/Dataset/PythonFinger/DataLogger/')
                    print('Program waits for the startlogger bit goes to FALSE')
         
        #Reset breakloop if it has appered
        if int(qStartCollect) == 0 and breakLoop == 1:
            breakLoop = 0
            print('Maxloop has been reset')
            print('Program ready for new datacollection to start')
            
    
    
finally:
    client.disconnect()
























 