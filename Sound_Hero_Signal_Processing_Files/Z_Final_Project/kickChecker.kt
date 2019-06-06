// This is pseudo code(ish)
// It has gaps that you need to fill in to fit in

// This is an array of time stamps (in ms) where a kick drum was detected
// It may be a good idea to read in these points from their .txt file
// Ideally we would want to get these values from doing the signal processing in Kotlin
// but I don't know if we'll have time for that.
// That might be something we put in the "future expansion" portion of the powerpoint
var beats = arrayof(5059
18772
32490
35907
36335
36764
37193
37632
38050
38478
38907
39335
39764
40193
40621
41050
41478
41907
42336
42764
43193
43621
44050
44489
44907
45335
45764
46193
46621
47050
47479
47907
48325
48764
49193
49621
50050
50478
50907
51336
51764
52192
52621
53049
53479
53907
54336
54764
55192
55621
56050
56478
56907
57336
57764
58203
58621
59049
59478
59907
60335
60764
61193
61621
62049
62478
63024
63142
63239
63331
71477
71907
72346
72764
73203
73632
74050
74478
74917
75335
75764
76203
76621
77049
77478
77907
78335
78764
79192
79621
80060
80478
80906
81335
81764
82192
82621
83050
83478
83907
84335
84763
85192
85621
86060
86478
86918
87335
87764
88203
88620
89049
89489
89907
90335
90764
91192
91620
92049
92478
92907
93345
93774
94203
94643
99343
114771
128488
131906
132335
132763
133192
133620
134049
134477
134906
135334
135763
136191
136620
137049
137477
137916
138335
138773
139192
139620
140049
140478
140906
141334
141763
142192
142620
143048
143477
143906
144335
144773
145191
145502
145620
146049
146477
146906
147334
147763
148191
148620
149048
149477
149905
150334
150763
151202
151620
152049
152477
152906
153334
153774
154192
154620
155049
155477
155905
156334
156762
157191
157620
158049
158477
159063
159197
159341
169483
169631
170048
170487
170906
171345
171762
172191
172620
173049
173476
173905
174333
174762
175191
175620
176048
176477
176906
177334
177763
178201
178620
179048
179487
179905
180334
180763
181191
181620
182048
182477
182905
183334
183763
184201
184620
185059
185476
185905
186324
186762
187191
187619
188048
188477
188915
189344
189763
190201
190620
191049
191477
191916
193627
197059)

var accR // This is the accelerometer reading
var accThresh = 18// This is the threshold that, if crossed, triggers a fist pump
// (18 is a good value for ythe arduino but idk about android)
var grace_window = 225 // This is the amount of time the accelerometer has to wait
// before counting an "above threshold" signal as a fist pump (200-250ms seems resonable)

var greenThresh = 20 // This is the distance from the actual beat to the user's attempt that gets a score of "green"
// (20 is a resonable number)
var yellowThresh = 40 // This is the distance from the actual beat to the user's attempt that gets a score of "yellow"
// (40 is a resonable number)
var prevAttempt = 0 // This variable keeps track of the time stamp of the previous succesful fist pump

if(accR > accThresh){
    checkUserBeat(accR)
}

fun checkUserBeat(accR: float){
    // I'm not sure what you called your media player or how to get milliseconds from it
    var attempt = MediaPlayer.getCurrentPosition() // This is the time stamp where the user might have "fist pumped"

    // This checks if enough time has passed before the next fist pump can be detected
    if(abs(attempt - prevAttempt)>grace_window){
        // Loops through all the values in "beats"
        var distances = arrayOfNulls<Number>(beats.size)
        for (i in array.indices) {
            distances[i] = abs(attempt-beats[i] // This is an array of all the distances from each beat to the user's attempt
            // We may need to subtract every distance by 20-40ms here.
            // Lets do that if the beat detector seems off
        }
        distances.sort()
        if(distances[0]<greenThresh){
            ble!!.send("green")
        } else if(distances[0]<yellowThresh){
            ble!!.send("yellow")
        } else{
            ble!!.send("red")
        }
        prevAttempt = attempt
    }
}