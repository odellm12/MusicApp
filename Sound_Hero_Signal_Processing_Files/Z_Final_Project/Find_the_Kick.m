% Find the Kick
% Mobile Final Project Signal Processing Prototype

clear;
clc;
close all;

[wav, Fs] = audioread('DOTA.wav');

fps = 24; % Frames per second
vl = 28; % Video length 29


% .-- This is how you filter with a difference equation
% |
% V
% H(z)= (1-z(-1)) / (1-3z(-1)+2z(-2))
% y(n)-3*y(n-1)+2*y(n-2) = x(n)-x(n-1);

% Filtering ---------------------------------------------------------------
% Section 1
k1 = 0.210437273851690309633966080582467839122;
num1 = k1*[1,-1.999715279155000358102256541315000504255,1];
den1 = [1,-1.99955689404341763193428960221353918314, 0.999763861879083393091605103109031915665];
% Difference equation
y1d = zeros(length(wav(:,1)),1);
for n = 3:length(wav(:,1))
    y1d(n) = (num1(1)*wav(n,1) + num1(2)*wav(n-1,1) + num1(3)*wav(n-2,1) - den1(2)*y1d(n-1) - den1(3)*y1d(n-2))/den1(1);
end
% y1 = filter(num1,den1,wav);

% Section 2
k2 = 0.210437273851690309633966080582467839122;
num2 = k2*[1,-1.999898937775592244747713266406208276749,1];
den2 = [1,-1.999667477171261698032367348787374794483, 0.999806477630654999444459463120438158512];
% Difference equation
y2d = zeros(length(y1d),1);
for n = 3:length(y1d)
    y2d(n) = (num2(1)*y1d(n) + num2(2)*y1d(n-1) + num2(3)*y1d(n-2) - den2(2)*y2d(n-1) - den2(3)*y2d(n-2))/den2(1);
end
% y2 = filter(num2,den2,y1);

% Section 3
k3 = 0.163929096939188917447793869541783351451;
num3 = k3*[1,-1.999620533527335819456993704079650342464,1];
den3 = [1,-1.999158249204060489034873171476647257805, 0.99935082980535538954569574343622662127];
% Difference equation
y3d = zeros(length(y2d),1);
for n = 3:length(y3d)
    y3d(n) = (num3(1)*y2d(n) + num3(2)*y2d(n-1) + num3(3)*y2d(n-2) - den3(2)*y3d(n-1) - den3(3)*y3d(n-2))/den3(1);
end
% y3 = filter(num3,den3,y2);

% Section 4
k4 = 0.163929096939188917447793869541783351451;
num4 = k4*[1,-1.999924172419381918075487192254513502121,1];
den4 = [1,-1.999279023516156161832668658462353050709, 0.999428349424154371938300300826085731387];
% Difference equation
y4d = zeros(length(y3d),1);
for n = 3:length(y3d)
    y4d(n) = (num4(1)*y3d(n) + num4(2)*y3d(n-1) + num4(3)*y3d(n-2) - den4(2)*y4d(n-1) - den4(3)*y4d(n-2))/den4(1);
end
% y4 = filter(num4,den4,y3);

% Section 5
k5 = 0.001308477824504156736620807954807332862;
num5 = k5*[1,0,-1];
den5 = [1,-1.999036145472885994678335919161327183247, 0.999205709629116700654094529454596340656];
% Difference equation
y5d = zeros(length(y4d),1);
for n = 3:length(y4d)
    y5d(n) = (num5(1)*y4d(n) + num5(2)*y4d(n-1) + num5(3)*y4d(n-2) - den5(2)*y5d(n-1) - den5(3)*y5d(n-2))/den5(1);
end
%y5 = filter(num5,den5,y4);

y5d = [0;0;0;0;0;0;0;0;0;0;y5d]; % restoring the orginal length


% Finding the FFTs of the original signal
[wavm, ftss, csl] = sgnlclms(wav,Fs,fps);
% wavm = The signal broken into columns
% ftss = The sample size of each column
% csl = The new length of the full signal once it has been clipped

[xsf, WAVft] = freqspecmtrx(wavm,Fs);
% xsf = Frequency columns
% WAVft = Magnitude columns

% Finding the FFTs 24 of the filtered signal
[wavm_fil, ftss_fil, csl_fil] = sgnlclms(y5d,Fs,fps);
% wavm = The signal broken into columns
% ftss = The sample size of each column
% csl = The new length of the full signal once it has been clipped

[xsf_fil, WAVft_fil] = freqspecmtrx(wavm_fil,Fs);
% xsf = Frequency columns
% WAVft = Magnitude columns

set(gcf, 'Position',  [100, 500, 1920, 1080])

% Determinig the time axis
t_wav = (0:(length(wav(:,1))-1))/Fs;
t_fil = (0:(length(y5d(:,1))-1))/Fs;

% Correcting the phase offset
t_fil_corrected = t_fil - .145;

th = .2; % Threshold
thresh = th*ones(1,length(y5d));



% Filtered animation
v = VideoWriter('Unfiltered_v_Filtered','MPEG-4');
v.FrameRate = fps;
open(v); 
%{
figure(1)
set(gcf, 'Position',  [100, 500, 1920, 1080])
for n = 1:vl*fps
    subplot(4,1,1)
    semilogx(xsf,WAVft(:,n),'linewidth',2)
    title('Song Frequency Spectrum')
    xlabel('Frequency [Hz]')
    ylabel('Magnitude')
    ylim([0 .6])
    
    subplot(4,1,2)
    plot(t_wav,wav(:,1))
    title('Original Signal')
    xlabel('Length [s]')
    ylabel('Magnitude')
    
    subplot(4,1,3)
    semilogx(xsf_fil,WAVft_fil(:,n),'linewidth',2)
    title('Filtered Song Frequency Spectrum')
    xlabel('Frequency [Hz]')
    ylabel('Magnitude')
    ylim([0 .4])
    
    subplot(4,1,4)
    plot(t_fil,y5d(:,1))
    hold on
    plot(t_fil,thresh)
    hold off
    title('Filtered Signal')
    xlabel('Length [s]')
    ylabel('Magnitude')

    drawnow
    frame = getframe(gcf);
    writeVideo(v,frame);
end
close(v)
%}

% Finding the kick
hits = zeros(1,length(y5d));
grace_samp = 4000;
count = -1;
hit_location = zeros(1,1000);
i = 1;
shift = 5000; % Number of samples to shift
for n = 1:length(y5d)
   if((y5d(n)>th) && (count < 0))
       hits(n) = th;
       count = grace_samp;
       hit_location(i) = n-shift;
       i = i + 1;
   else
       count = count-1;
   end
end

figure(2)
set(gcf, 'Position',  [100, 500, 1920, 1080])
plot(y5d)
hold on
plot(hits,'linewidth',2)
hold off
xlim([4E5,6E5])
title('100Hz Peak Detection v. Filtered Sound File')
xlabel('Samples')
ylabel('Amplitude')
legend('Filtered Sound File','100 Hz Peak Detection')

shifted_hits = zeros(1,length(hits));
hits = [hits,zeros(1,shift)];
for n = 1:(length(shifted_hits))
    shifted_hits(n) = hits(n+shift);
end

here = ones(1,length(hit_location));

figure(3)
set(gcf, 'Position',  [100, 500, 1920, 1080])
plot(wav(:,1))
hold on
plot(4*hits,'linewidth',2)
plot(4*shifted_hits,'linewidth',2)
plot(hit_location,.8*ones(1,length(hit_location)),'x')
hold off
%xlim([4E5,6E5])
title('Shifted Kick Detection v. Original Sound File')
xlabel('Samples')
ylabel('Amplitude')
legend('Original Sound File','100 Hz Peaks','Shifted Kick Detection')

% Checking if the hits are on time
[beep, Fs_beep] = audioread('beep.wav');
beep_on_time = conv(beep(:,1),2*shifted_hits');
check = wav(:,1) + beep_on_time(1:length(wav(:,1)));

figure(4)
set(gcf, 'Position',  [100, 500, 1920, 1080])
subplot(3,1,1)
plot(beep)
title('Beep')
xlabel('Samples')
ylabel('Magnitude')
subplot(3,1,2)
plot(shifted_hits)
title('Locations of Kick Drum Hits')
xlabel('Samples')
ylabel('Magnitude')
subplot(3,1,3)
plot(beep_on_time)
title('Beep Convolved with Locations of Kick Drum Hits')
xlabel('Samples')
ylabel('Magnitude')

rh = importdata('Rounded_Hits.txt');
rhk = importdata('Rounded_Hits_Kotlin.txt');
figure(5)
set(gcf, 'Position',  [100, 500, 1920, 1080])
plot(rh,'.')
hold on
plot(rhk,'o')
title('MATLAB Processed v. Kotlin Processed Hit Locations')
xlabel('Hit Number')
ylabel('Location [mS]')
legend('MATLAB Processed','Kotlin Processed')

% format long g
% milli_hits = round(hit_location'*(1000/Fs))
% fileID = fopen('Rounded_Hits.txt','w');
% fprintf(fileID,'%d\n',milli_hits);
% fclose(fileID);
% close all

%soundsc(check,Fs)
%audiowrite('DOTA_with_beeps.wav',check,Fs)
