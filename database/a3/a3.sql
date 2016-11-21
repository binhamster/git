use johnson330;

select Ticker, min(TransDate), max(TransDate),
 count(distinct TransDate) as TradingDays
 from Company natural join PriceVolume
 where Industry = 'Telecommunications Services'
 and TransDate >= '2005.02.09' and TransDate <= '2014.08.18'
 group by Ticker
 having TradingDays >= 150
 order by Ticker;

select max(TD), min(TD2) from (
select Ticker, min(TransDate) as TD, max(TransDate) as TD2,
 count(distinct TransDate) as TradingDays
 from Company natural join PriceVolume
 where Industry = 'Telecommunications Services'
 group by Ticker
 having TradingDays >= 150
 order by Ticker) AS X;
 
select Industry
from Company natural join PriceVolume
group by Industry
order by Industry ASC;

select P.Ticker, P.TransDate, P.openPrice, P.closePrice
 from PriceVolume P natural join Company
 where Industry = 'Telecommunications Services'
 and TransDate >= '2005.02.09' and TransDate <= '2014.08.18'
 order by TransDate, Ticker;
 
 select P.Ticker, P.TransDate, P.openPrice, P.closePrice
 from PriceVolume P natural join Company
 where Industry = 'Telecommunications Services'
 and TransDate >= '2006.07.14' and TransDate <= '2006.07.17';

 
select P.TransDate, P.openPrice, P.closePrice
 from PriceVolume P
 where Ticker = 'AMT' and TransDate >= '2006.07.14'
 and TransDate <= '2014.08.18';

select count(T) from (
select Ticker as T
from Company natural join PriceVolume
where Industry = 'Telecommunications Services'
group by Ticker) AS X;

select Ticker
from Company natural join PriceVolume
where Industry = 'Telecommunications Services'
group by Ticker
order by Ticker ASC;

select * from PriceVolume where Ticker = 'INTC' 

