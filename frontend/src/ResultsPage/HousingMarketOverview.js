import React, { useState, useEffect } from 'react';
import styles from './HousingMarketOverview.module.css';

const HousingMarketOverview = ({ regionId = 'United States' }) => {
    const [marketData, setMarketData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                setLoading(true);
                const response = await fetch(`http://localhost:8080/api/housing-market/${regionId}`);

                if (!response.ok) {
                    throw new Error(`API returned status: ${response.status}`);
                }

                const data = await response.json();
                setMarketData(data);
            } catch (err) {
                console.error("Error fetching housing market data:", err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [regionId]);

    if (loading) return <div className={styles.loading}>Loading housing market data...</div>;
    if (error) return <div className={styles.error}>Error: {error}</div>;
    if (!marketData) return null;

    // Calculate price trend (percentage change from first to last month)
    const priceHistory = marketData.priceHistory || {};
    const historyEntries = Object.entries(priceHistory).sort((a, b) => new Date(a[0]) - new Date(b[0]));

    let priceTrend = 0;
    if (historyEntries.length >= 2) {
        const oldestPrice = historyEntries[0][1];
        const newestPrice = historyEntries[historyEntries.length - 1][1];
        priceTrend = ((newestPrice - oldestPrice) / oldestPrice) * 100;
    }

    const trendDirection = priceTrend >= 0 ? 'up' : 'down';
    const trendColor = trendDirection === 'up' ? styles.trendUp : styles.trendDown;
    const trendSymbol = trendDirection === 'up' ? '↑' : '↓';

    // Format date for last updated
    const lastUpdated = new Date(marketData.lastUpdated).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });

    return (
        <div className={styles.container} id="housing-market">
            <h2 className={styles.title}>Housing Market Overview - {marketData.regionName}</h2>

            <div className={styles.statsContainer}>
                <div className={styles.statsCard}>
                    <h3>Median Price</h3>
                    <p className={styles.stat} data-prefix="$">{marketData.medianPrice.toLocaleString()}</p>
                </div>

                <div className={styles.statsCard}>
                    <h3>Days on Market</h3>
                    <p className={styles.stat}>{Math.round(marketData.averageDaysOnMarket)} days</p>
                </div>

                <div className={styles.statsCard}>
                    <h3>Price Per Sq Ft</h3>
                    <p className={styles.stat} data-prefix="$">{Math.round(marketData.medianPricePerSquareFoot)}</p>
                </div>

                <div className={styles.statsCard}>
                    <h3>Median Rent</h3>
                    <p className={styles.stat} data-prefix="$">{marketData.medianRent.toLocaleString()}</p>
                </div>
            </div>

            <div className={styles.listingInfo}>
                <span className={styles.newListings}>{marketData.newListings.toLocaleString()} new listings</span>
                <span className={styles.totalListings}>{marketData.totalListings.toLocaleString()} total active listings</span>
                <span className={styles.lastUpdated}>Last updated: {lastUpdated}</span>
            </div>

            <div className={styles.trendsContainer}>
                <h3>Price Trends (3 Months)</h3>

                <div className={styles.trendBox}>
                    <div className={styles.trendValue}>
                        <span className={`${styles.trendPercent} ${trendColor}`}>
                            {trendSymbol} {Math.abs(priceTrend).toFixed(1)}%
                        </span>
                        <span className={styles.trendPeriod}>Last 3 months</span>
                    </div>

                    <div className={styles.priceHistory}>
                        {historyEntries.map(([month, price], index) => (
                            <div key={month} className={styles.pricePoint}>
                                <div
                                    className={styles.priceBar}
                                    style={{
                                        height: `${(price / marketData.medianPrice) * 100}%`,
                                        animationDelay: `${index * 0.2}s`
                                    }}
                                ></div>
                                <div className={styles.priceMonth}>{month.substring(5)}</div>
                                <div className={styles.priceValue}>${(price / 1000).toFixed(0)}k</div>
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default HousingMarketOverview;