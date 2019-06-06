% [frequency spectrum columns, magnitude collumns] = freqspecmtrx(signal columns, sample rate)
% This function takes a matrix of signals and returns a frequency matrix.
%               !!!The signals must come in columns!!!

function [xm, ym] = freqspecmtrx(wavm, Fs)

smpsz = length(wavm(:,1)); % The sample size

WAVm = fft(wavm); % The wav file in the frequency domain
WAVmag = abs(WAVm/smpsz); % Magnitudes of WAV
WAVss = WAVmag(1:smpsz/2+1,:); % Single sided magnitude of WAV
xsf = Fs*(0:(smpsz/2))/smpsz; % x axis scaled to frequencies

xm = xsf; % The frequency spectrum
ym = 2*WAVss; % The associated magnitudes scaled

end