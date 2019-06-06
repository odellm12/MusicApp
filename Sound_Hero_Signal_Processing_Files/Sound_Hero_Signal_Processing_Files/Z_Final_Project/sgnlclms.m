% [signal columns, the sample size of each collumn, the clipped length of the full signal] = freqspecmtrx(signal, sample rate, columns/s)
% This function takes a signal and the column sample rate.
% (How many columns to create per second)
% It returns the signal split into collumns.
% The signal is clipped to attain a rectangular matrix.

function [wavm, smpsz, csl] = sgnlclms(wav, Fs, clmFs)

sgnlngth = length(wav); % The signal length
smpsz = round(Fs/clmFs); % The fourier transform sample size
numsmps = round(sgnlngth/smpsz)-1; % Number of fourier transform samples
csl = numsmps*smpsz; % Clipped signal length (to fit into columns)
cwav = wav(1:csl); % The clipped wave
wavm = reshape(cwav,[smpsz,numsmps]);% The wav file in matrix form

end