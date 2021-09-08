// *******************************************************************************
// * Copyright 2007,2011,2017 ESA - European Space Agency
// * This code is provided under the terms of the ESA community license type 2, version 1.2
// ******************************************************************************
//
// <!-- Advanced Planning and Scheduling Initiative (APSI) -->
// <!-- APSI Timeline Representation Framework (APSI-TRF) - Version 3.3.5 -->
//
// @author Simone Fratini, Julian Gorfer
// @version 0.14

DOMAIN Observe_Area {

    TEMPORAL_MODULE temporal_module = DATE [2020-01-01T00:00:00.000Z,2020-12-31T23:59:59.000Z],3000;
    //DOMAIN_TIMELINE GENERIC mission_timeline1;
    DOMAIN_TIMELINE FLEXIBLE mission_timeline1;

    // Coordinates x1000, planner uses integers
    DATA_TYPE NUMERIC latitude = [-90000, +90000];
    DATA_TYPE NUMERIC longitude = [-180000, +180000];
    DATA_TYPE ENUMERATION boolean = {T,F};
    DATA_TYPE ENUMERATION class_types = {cloudy,clear};
    DATA_TYPE ENUMERATION ident_types = {none,volcano,land,water,island,human_made};
    DATA_TYPE OBJECT file_id;

    COMP_TYPE STATE_VARIABLE GPSSystem
    VALUES
    {
        Visible(latitude, longitude) [00:00:01,+INF];
        NotVisible() [00:00:01,+INF];
    }
    TRANSITIONS
    {
        NotVisible() TO {
            Visible(?x_target, ?y_target);
        }
        Visible(?x_target, ?y_target) TO {
            NotVisible();
        }
    }

    COMP_TYPE STATE_VARIABLE Camera
    VALUES
    {
        CamIdle() [00:00:01,+INF];
        MakePic(latitude, longitude, file_id) [00:00:04,00:00:04];
    }
    TRANSITIONS
    {
        CamIdle() TO { MakePic(?x_target, ?y_target, ?file_id); }
        MakePic(?x_target, ?y_target, ?file_id) TO { CamIdle(); }
    }


    COMP_TYPE STATE_VARIABLE MissionTimeline
    VALUES
    {
        MTIdle() [00:00:01,+INF];
        TakePicture(latitude, longitude, file_id, boolean, boolean) [00:00:01,+INF];
    }
    TRANSITIONS
    {
        MTIdle() TO { TakePicture(?x_target, ?y_target, ?file_id, ?store, ?identify); }
        TakePicture(?x_target, ?y_target, ?file_id, ?store, ?identify) TO { MTIdle(); }
    }

    COMP_TYPE STATE_VARIABLE Attitude
    VALUES
    {
        Unlocked() [00:00:01,+INF];
        Locking(latitude, longitude) [00:00:05,00:00:05];
        Locked(latitude, longitude) [00:00:10,00:00:10];
    }
    TRANSITIONS
    {
        Unlocked() TO { Locking(?x_new, ?y_new); }
        Locking(?x_new, ?y_new) TO { Locked(?x_new, ?y_new); }
        Locked(?x_new, ?y_new) TO { Unlocked(); }
    }

    COMP_TYPE STATE_VARIABLE Classification
    VALUES
    {
        ClassIdle() [00:00:01,+INF];
        ClassifyClouds(file_id,class_types) [00:00:01,+INF];
        SaveImage(file_id) [00:00:01,+INF];
    }
    TRANSITIONS
    {
        ClassIdle() TO { ClassifyClouds(?file_id,?t); }
        ClassifyClouds(?file_id,?t) TO { SaveImage(?file_id); ClassIdle(); ?t = clear;}
        ClassifyClouds(?file_id,?t) TO { ClassIdle(); ?t = cloudy;}
        SaveImage(?file_id) TO { ClassIdle(); }
    }

    COMP_TYPE RESERVOIR_RESOURCE MemoryType;


    COMPONENT MissionTimeline MT {FLEXIBLE <COMPLETE> tl;}
    COMPONENT GPSSystem GPS { FLEXIBLE <COMPLETE> tl; }
    COMPONENT Attitude ATT { FLEXIBLE <COMPLETE> tl; }
    COMPONENT Camera CAM {FLEXIBLE <COMPLETE> tl;}
    COMPONENT Classification CLAS { FLEXIBLE <COMPLETE> tl; }
    COMPONENT MemoryType MEM {FLEXIBLE tl(0.0,2014751733760.0);}

    SYNCHRONIZE MT.tl
    {
        VALUE TakePicture(?x_target, ?y_target, ?file_id, T, T)
        {
            op1 CAM.tl.MakePic(?x_target, ?y_target, ?file_id);
            op2 <?> GPS.tl.Visible(?x_target, ?y_target);
            op3 ATT.tl.Locked(?x_target, ?y_target);
            op4 CLAS.tl.ClassifyClouds(?file_id,clear);
			op5 CLAS.tl.SaveImage(?file_id);

            CONTAINS [5,+INF][5,+INF] op1;
            op1 DURING [0,+INF][0,+INF] op2;
            op1 DURING [0,+INF][0,+INF] op3;
            op3 DURING [0,+INF][0,+INF] op2;

            op4 MEETS op5;
            op1 CONTAINS op4;
            op1 CONTAINS op5;
        }
    }


    SYNCHRONIZE CLAS.tl
    {
        VALUE SaveImage(?file_id)
        {
            act1 MEM.tl.ACTIVITY(0.018);

            DURATION [00:00:03,00:00:05];
            EQUALS act1;
        }
    }
}